import React from 'react';
import Avatar, { AvatarProps } from '@mui/material/Avatar';
import IconButton, { IconButtonProps } from '@mui/material/IconButton';
import Button, { ButtonProps} from '@mui/material/Button';
import { styled } from '@mui/material/styles';
import { useAuth } from "react-oidc-context";

const UserAvatar = styled(Avatar, {})<AvatarProps>(({ theme }) => {
    return {
        backgroundColor: theme.palette.secondary.main,
    };
});

const SignInButton = styled(Button, {})<ButtonProps>(({ theme }) => {
    return {
        backgroundColor: theme.palette.secondary.main,
        
    };
});

export default function SSOUserAvatar() {

    const auth = useAuth();

    if (auth.isLoading) {
        return <div>Loading...</div>;

    } else if (auth.error) {
        return <div>Oops... {auth.error.message}</div>;

    } else if (auth.isAuthenticated) {
        
        let username = auth.user?.profile.sub;
        let lastName: string = (auth.user?.profile.last_name as string);
        let firstName: string = (auth.user?.profile.first_name as string);

        let initials = username?.slice(0, username?.indexOf('@'))
            .split('.')
            .map((word) => word[0])
            .join('')
            .toUpperCase();

        // create initials string from first and last name if available
        if (firstName && lastName) {
            initials = firstName[0] + lastName[0];
        }

        return (
            <IconButton color="inherit" onClick={() => auth.signoutRedirect()}>
                <UserAvatar> {initials} </UserAvatar>
            </IconButton>
        );

    } else {
        return (
            <SignInButton 
                variant="contained" 
                onClick={
                    () => auth.signinRedirect()
                }>
                    Sign In
            </SignInButton>);

    }
}
