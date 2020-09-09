/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.karaf.shell.ssh;

import java.io.Closeable;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Map;
import javax.security.auth.Subject;

import jline.Terminal;
import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Function;
import org.apache.karaf.shell.console.jline.Console;
import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.SessionAware;
import org.apache.sshd.server.session.ServerSession;
import org.osgi.service.blueprint.container.ReifiedType;

/**
 * SSHD {@link org.apache.sshd.server.Command} factory which provides access to Shell.
 */
public class ShellFactoryImpl implements Factory<Command> {

    private CommandProcessor commandProcessor;

    public void setCommandProcessor(CommandProcessor commandProcessor) {
        this.commandProcessor = commandProcessor;
    }

    public Command create() {
        return new ShellImpl();
    }

    public class ShellImpl implements Command, SessionAware
    {
        private InputStream in;

        private OutputStream out;

        private OutputStream err;

        private ExitCallback callback;

        private ServerSession session;

        private boolean closed;

        public void setInputStream(final InputStream in) {
            this.in = in;
        }

        public void setOutputStream(final OutputStream out) {
            this.out = out;
        }

        public void setErrorStream(final OutputStream err) {
            this.err = err;
        }

        public void setExitCallback(ExitCallback callback) {
            this.callback = callback;
        }

        public void setSession(ServerSession session) {
            this.session = session;
        }

        public void start(final Environment env) throws IOException {
            try {
                final Terminal terminal = new SshTerminal(env);
                String encoding = env.getEnv().get("LC_CTYPE");
                if (encoding != null && encoding.indexOf('.') > 0) {
                    encoding = encoding.substring(encoding.indexOf('.') + 1);
                }
                Console console = new Console(commandProcessor,
                        in,
                        new PrintStream(new LfToCrLfFilterOutputStream(out), true),
                        new PrintStream(new LfToCrLfFilterOutputStream(err), true),
                        terminal,
                        //encoding,
                        new Runnable() {
                            public void run() {
                                destroy();
                            }
                        });
                final CommandSession session = console.getSession();
                session.put("APPLICATION", System.getProperty("karaf.name", "root"));
                for (Map.Entry<String,String> e : env.getEnv().entrySet()) {
                    session.put(e.getKey(), e.getValue());
                }
                session.put("#LINES", new Function() {
                    public Object execute(CommandSession session, List<Object> arguments) throws Exception {
                        return Integer.toString(terminal.getHeight());
                    }
                });
                session.put("#COLUMNS", new Function() {
                    public Object execute(CommandSession session, List<Object> arguments) throws Exception {
                        return Integer.toString(terminal.getWidth());
                    }
                });
                session.put(".jline.terminal", terminal);
                new Thread(console) {
                    @Override
                    public void run() {
                        Subject subject = ShellImpl.this.session != null ? ShellImpl.this.session.getAttribute(KarafJaasAuthenticator.SUBJECT_ATTRIBUTE_KEY) : null;
                        if (subject != null) {
                            Subject.doAs(subject, new PrivilegedAction<Object>() {
                                public Object run() {
                                    doRun();
                                    return null;
                                }
                            });
                        } else {
                            doRun();
                        }
                    }
                    protected void doRun() {
                        super.run();
                    }
                }.start();
            } catch (Exception e) {
                throw (IOException) new IOException("Unable to start shell").initCause(e);
            }
        }

        public void destroy() {
            if (!closed) {
                closed = true;
                ShellFactoryImpl.flush(out, err);
                ShellFactoryImpl.close(in, out, err);
                callback.onExit(0);
            }
        }

    }

    private static void flush(OutputStream... streams) {
        for (OutputStream s : streams) {
            try {
                s.flush();
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    private static void close(Closeable... closeables) {
        for (Closeable c : closeables) {
            try {
                c.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    public static Converter getConverter() {
        return new Converter();
    }

    public static class Converter implements org.osgi.service.blueprint.container.Converter {

        public boolean canConvert(Object sourceObject, ReifiedType targetType) {
            return ShellFactoryImpl.class.isAssignableFrom(sourceObject.getClass())
                    && Factory.class.equals(targetType.getRawClass())
                    && Command.class.equals(targetType.getActualTypeArgument(0).getRawClass());
        }

        public Object convert(Object sourceObject, ReifiedType targetType) throws Exception {
            return sourceObject;
        }
    }

    // TODO: remove this class when sshd use lf->crlf conversion by default
    public class LfToCrLfFilterOutputStream extends FilterOutputStream {

        private boolean lastWasCr;

        public LfToCrLfFilterOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        public void write(int b) throws IOException {
            if (!lastWasCr && b == '\n') {
                out.write('\r');
                out.write('\n');
            } else {
                out.write(b);
            }
            lastWasCr = b == '\r';
        }

    }


}
