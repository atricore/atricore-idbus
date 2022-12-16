import { AuthContextProps, useAuth } from "react-oidc-context";

import * as React from 'react';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import List from '@mui/material/List';
import ListSubheader from '@mui/material/ListSubheader';
import Box from '@mui/material/Box';
import IconButton from '@mui/material/IconButton';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import PeopleIcon from '@mui/icons-material/Person2';
import AttrIcon from '@mui/icons-material/Label';
import EmailIcon from '@mui/icons-material/Email';
import GroupIcon from '@mui/icons-material/Group';
import DNIcon from '@mui/icons-material/Key';
import IDIcon from '@mui/icons-material/PermIdentity';


function SSOClaims(auth: AuthContextProps) {

    const p = auth.user?.profile
    if (p === undefined) {
        return <div>Oops... no profile information</div>;
    }

    // reduce the profile object to a list of key-value pairs
    let claims = Object.keys(p).filter((key)=> key != 'dn').sort().map((key) => {
        return (
            <TableRow
              key={key + '-row'}
              sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
            >

             <TableCell component="th" scope="row">
              <Box><IconButton>{ClaimIcon(key)}</IconButton> {ClaimLabel(key)}</Box>
              </TableCell>
              <TableCell align="right">{(p[key] as string)}</TableCell>
            </TableRow>

        )
    })
    // create the list items
    return claims

}

function ClaimLabel(claim: string) {
    if (claim === 'email') {
        return 'Email'
    } else if (claim === 'sub') {
        return 'Subject'
    } else if (claim === 'groups') {
        return 'Groups'
    } else if (claim === 'first_name') {
        return 'First Name'
    } else if (claim === 'last_name') {
        return 'Last Name'
    } else if (claim === 'josso.user.dn') {
        return 'DN'
    } else {
        return claim
    }
}

function ClaimIcon(claim: string) {
    if (claim === 'email') {
        return <ListItemIcon>
            <EmailIcon />
        </ListItemIcon>
    } else if (claim === 'sub') {
        return <ListItemIcon>
            <IDIcon />
        </ListItemIcon>
    } else if (claim === 'groups') {
        return <ListItemIcon>
            <GroupIcon />
        </ListItemIcon>
    } else if (claim === 'josso.user.dn') {
        return <ListItemIcon>
            <DNIcon />
        </ListItemIcon>
    } else {
        return <ListItemIcon>
            <AttrIcon />
        </ListItemIcon>
    }
}


export default function SSOProfile() {
    const auth = useAuth();
    if (auth.isLoading) {
        return <div>Loading...</div>;

    } else if (auth.error) {
        return <div>Oops... {auth.error.message}</div>;

    } else if (auth.isAuthenticated) {
        let username = auth.user?.profile.sub;


        return (

            <TableContainer component={Paper}>
                <Table sx={{ minWidth: 650 }} aria-label="simple table">
                    <TableHead>
                    <TableRow>
                        <TableCell align="right">Claim</TableCell>
                        <TableCell align="right">Value</TableCell>
                    </TableRow>
                    </TableHead>
                    
                    <TableBody>
                        {SSOClaims(auth)}
                    </TableBody>
                </Table>
            </TableContainer>

        )

    } else {
        return <div>Not authenticated</div>;
    }
}

/*
<Grid container rowSpacing={1} columnSpacing={{ xs: 1, sm: 2, md: 3 }}>
  <Grid xs={6}>
    <Item>1</Item>
  </Grid>
  <Grid xs={6}>
    <Item>2</Item>
  </Grid>
  <Grid xs={6}>
    <Item>3</Item>
  </Grid>
  <Grid xs={6}>
    <Item>4</Item>
  </Grid>
</Grid>
*/


