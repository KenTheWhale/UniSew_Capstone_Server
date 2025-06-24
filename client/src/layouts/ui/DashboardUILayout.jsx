import {ReactRouterAppProvider} from "@toolpad/core/react-router";
import {Outlet, useNavigate} from 'react-router-dom'
import {useMemo, useState} from "react";
import {Account, AccountPreview, DashboardLayout, SignOutButton} from "@toolpad/core";
import {Chip, createTheme, Divider, IconButton, Stack, Typography} from "@mui/material";
import '../../styles/ui/DashboardUILayout.css'
import {Logout} from '@mui/icons-material';
import {enqueueSnackbar} from "notistack";

function CustomAppTitle(title) {
    return(
        <Stack direction="row" alignItems="center" spacing={2}>
            <img src={'/logo.png'} alt="UniSew" width={40} height={40} />
            <Typography variant="h6">UniSew</Typography>
            <Chip size="small" label={title} variant={"filled"} color={"secondary"} sx={{textTransform: 'uppercase'}}/>
        </Stack>
    )
}

function CustomSignOutButton(){
    const navigate = useNavigate();

    return (
        <IconButton color={"error"} size={"medium"} onClick={()=> navigate("/sign-in")}>
            <Logout/>
        </IconButton>
    )
}

function AccountSidebarPreview(props) {
    const {handleClick, open, mini} = props;
    return (
        <Stack direction="column" p={0}>
            <Divider/>
            <AccountPreview
                variant={mini ? 'condensed' : 'expanded'}
                handleClick={handleClick}
                open={open}
                slots={{
                    moreIconButton: CustomSignOutButton
                }}
            />
        </Stack>
    );
}

const createPreviewComponent = (mini) => {
    function PreviewComponent(props) {
        return <AccountSidebarPreview {...props} mini={mini}/>;
    }

    return PreviewComponent;
};

function SidebarFooterAccount({mini}) {
    const PreviewComponent = useMemo(() => createPreviewComponent(mini), [mini]);
    return (
        <div>
            <Account
                slots={{
                    preview: PreviewComponent,
                }}
            />
            <Typography
                variant="caption"
                sx={{marginLeft: '1.5vw', whiteSpace: 'nowrap', overflow: 'hidden'}}
            >
                {mini ? 'UniSew' : `© ${new Date().getFullYear()} UniSew, All Rights Reserved.`}
            </Typography>
        </div>
    );
}

export default function DashboardUILayout({navigation, header, title}) {
    document.title = title;

    const user = JSON.parse(localStorage.getItem('user'))

    const [session, setSession] = useState({
            user: {
                name: user.profile.name,
                email: user.email,
                image: user.profile.avatar,
            }
        }
    );

    if(localStorage.getItem('message') && localStorage.getItem('variant')){
        enqueueSnackbar(localStorage.getItem('message'), {variant: localStorage.getItem('variant')})
        localStorage.removeItem('message')
        localStorage.removeItem('variant')
    }


    const authentication = {
        signIn: () => {
            setSession(session);
        }
    };

    const theme = createTheme({
        colorSchemes: {light: true, dark: false}
    })

    return (
        <ReactRouterAppProvider
            navigation={navigation}
            session={session}
            authentication={authentication}
            theme={theme}
        >
            <DashboardLayout
                disableCollapsibleSidebar
                slots={{
                    toolbarAccount: () => null,
                    sidebarFooter: SidebarFooterAccount,
                    appTitle: () => CustomAppTitle(header),
                }}
            >
                <div className={'outlet-container'}>
                    <Outlet/>
                </div>
            </DashboardLayout>
        </ReactRouterAppProvider>
    )
}