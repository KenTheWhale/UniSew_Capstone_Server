import {ReactRouterAppProvider} from "@toolpad/core/react-router";
import {Outlet, useNavigate} from 'react-router-dom'
import {useMemo, useState} from "react";
import {Account, AccountPreview, DashboardLayout, SignOutButton} from "@toolpad/core";
import {
    Avatar,
    Box,
    Button, Card, CardActions, CardContent,
    Chip,
    Divider, Fade,
    IconButton, ListItemIcon,
    Menu,
    MenuItem,
    Stack,
    Tooltip,
    Typography
} from "@mui/material";
import '../../styles/ui/DashboardUILayout.css'
import {AccountCircle, Logout, PersonAdd, Settings} from '@mui/icons-material';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import {Chip, createTheme, Divider, IconButton, Stack, Typography} from "@mui/material";
import '../../styles/ui/DashboardUILayout.css'
import {Logout} from '@mui/icons-material';
import {enqueueSnackbar} from "notistack";

function CustomAppTitle(title) {

    const [anchorEl, setAnchorEl] = useState(null);
    const open = Boolean(anchorEl);
    const handleClick = (event) => {
        setAnchorEl(event.currentTarget);
    };
    const handleClose = () => {
        setAnchorEl(null);
    };


    return(
        <Stack direction="row" alignItems="center" spacing={2}>
            <img src={'/logo.png'} alt="UniSew" width={40} height={40} />
            <Typography variant="h6">UniSew</Typography>
            <Chip size="small" label={title} variant={"filled"} color={"secondary"} sx={{textTransform: 'uppercase'}}/>
            <Box sx={{ display: 'flex', alignItems: 'center', textAlign: 'center', position: 'absolute', right: 80}}>
                <Tooltip title="Account settings">
                    <IconButton
                        onClick={handleClick}
                        size="small"
                        sx={{ ml: 2 }}
                        aria-controls={open ? 'account-menu' : undefined}
                        aria-haspopup="true"
                        aria-expanded={open ? 'true' : undefined}
                    >
                        <Avatar sx={{ width: 32, height: 32 }}>M</Avatar>
                    </IconButton>
                </Tooltip>
            </Box>
            <Menu
                anchorEl={anchorEl}
                id="account-menu"
                open={open}
                onClose={handleClose}
                onClick={handleClose}
                slotProps={{
                    paper: {
                        elevation: 0,
                        sx: {
                            overflow: 'visible',
                            filter: 'drop-shadow(0px 2px 8px rgba(0,0,0,0.32))',
                            mt: 1.5,
                            '& .MuiAvatar-root': {
                                width: 32,
                                height: 32,
                                ml: -0.5,
                                mr: 1,
                            },
                            '&::before': {
                                content: '""',
                                display: 'block',
                                position: 'absolute',
                                top: 0,
                                right: 14,
                                width: 10,
                                height: 10,
                                bgcolor: 'background.paper',
                                transform: 'translateY(-50%) rotate(45deg)',
                                zIndex: 0,
                            },
                        },
                    },
                }}
                transformOrigin={{ horizontal: 'right', vertical: 'top' }}
                anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
            >
                <MenuItem onClick={handleClose}>
                    <Avatar /> Profile
                </MenuItem>
                <MenuItem onClick={handleClose}>
                    <Avatar /> My account
                </MenuItem>
                <Divider />
                <MenuItem onClick={handleClose}>
                    <ListItemIcon>
                        <PersonAdd fontSize="small" />
                    </ListItemIcon>
                    Add another account
                </MenuItem>
                <MenuItem onClick={handleClose}>
                    <ListItemIcon>
                        <Settings fontSize="small" />
                    </ListItemIcon>
                    Settings
                </MenuItem>
                <MenuItem onClick={handleClose}>
                    <ListItemIcon>
                        <Logout fontSize="small" />
                    </ListItemIcon>
                    Logout
                </MenuItem>
            </Menu>








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