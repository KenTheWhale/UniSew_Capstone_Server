import {ReactRouterAppProvider} from "@toolpad/core/react-router";
import {Outlet, useNavigate} from 'react-router-dom'
import {useMemo, useState} from "react";
import {Account, AccountPreview, DashboardLayout, SignOutButton} from "@toolpad/core";
import {Chip, Divider, IconButton, Stack, Typography} from "@mui/material";
import '../../styles/ui/DashboardUILayout.css'
import {Logout} from '@mui/icons-material';

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
        <IconButton color={"error"} size={"medium"} onClick={()=> navigate("/home")}>
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

    const [session, setSession] = useState({
            user: {
                name: 'Mr Test',
                email: 'test@gmail.com',
                image: '/logo.png',
            }
        }
    );

    const authentication = {
        signIn: () => {
            setSession({
                user: {
                    name: 'Mr Test',
                    email: 'test@gmail.com',
                    image: '/logo.png',
                }
            });
        }
    };

    return (
        <ReactRouterAppProvider
            navigation={navigation}
            session={session}
            authentication={authentication}
        >
            <DashboardLayout
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