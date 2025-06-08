import '../../styles/auth/SignUp.css'
import {
    Button,
    Divider,
    FormControl, IconButton,
    InputAdornment,
    InputLabel, Link,
    OutlinedInput,
    TextField,
    Typography
} from "@mui/material";
import {useState} from "react";
import {Visibility, VisibilityOff} from '@mui/icons-material';

function RenderPasswordField({text, name}) {
    const [showPassword, setShowPassword] = useState(false);

    const handleClickShowPassword = () => setShowPassword((show) => !show);

    const handleMouseDownPassword = (event) => {
        event.preventDefault();
    };

    return (
        <FormControl sx={{my: 1}} fullWidth variant="outlined">
            <InputLabel size="small">
                {text} *
            </InputLabel>
            <OutlinedInput
                type={showPassword ? 'text' : 'password'}
                name={name}
                size="small"
                endAdornment={
                    <InputAdornment position="end">
                        <IconButton
                            onClick={handleClickShowPassword}
                            onMouseDown={handleMouseDownPassword}
                            edge="end"
                            size="small"
                        >
                            {showPassword ? (
                                <VisibilityOff sx={{color: 'white'}} fontSize="inherit"/>
                            ) : (
                                <Visibility sx={{color: 'white'}} fontSize="inherit" />
                            )}
                        </IconButton>
                    </InputAdornment>
                }
            >

            </OutlinedInput>
        </FormControl>
    )
}

function RenderPage() {
    return (
        <div className={"sign-up-main"}>
            <div className={"sign-up-container"}>
                <main className={"container-content"}>
                    <div className={"area"}>
                        <img alt="UniSew" width="50" height="50" src="/logo.png"/>
                        <Typography variant={'h4'} className="title">SIGN UP</Typography>
                        <Typography variant={'body2'} className="subtitle">UniSew</Typography>
                        <div className="input-area">
                            <div className="google-area">
                                <Button variant={"contained"}
                                        startIcon={<svg xmlns="http://www.w3.org/2000/svg" height="24"
                                                        viewBox="0 0 24 24" width="24">
                                            <path
                                                d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                                                fill="#4285F4"></path>
                                            <path
                                                d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                                                fill="#34A853"></path>
                                            <path
                                                d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                                                fill="#FBBC05"></path>
                                            <path
                                                d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                                                fill="#EA4335"></path>
                                            <path d="M1 1h22v22H1z" fill="none"></path>
                                        </svg>}>
                                    <span className={'ms-2'}>Sign up with Google</span>
                                </Button>
                            </div>
                            <Divider
                                textAlign={"center"}
                                className={'divider'}
                                sx={{
                                    color: 'white',
                                    '&::before, &::after': {borderTop: 1, borderColor: '#3c3f3b'},
                                    margin: '2vh 0'
                                }}
                            >
                                Or
                            </Divider>
                            <TextField label="Email *" name="email" type="email" size="small" fullWidth variant="outlined" sx={{my: 1}}/>
                            <TextField label="Name *" name="name" type="text" size="small" fullWidth variant="outlined" sx={{my: 1}}/>
                            <RenderPasswordField name={"password"} text={"Password"}/>
                            <RenderPasswordField name={"cpassword"} text={"Confirmed password"}/>
                            <Link underline={"none"} href={"/sign-in"} variant={'body2'} sx={{color: '#90caf9'}}>Already have account ? Sign in</Link>
                            <Button variant={"contained"} size={"medium"} fullWidth sx={{margin: '2vh 0', backgroundColor: '#90caf9', color: 'black'}}>
                                Sign up
                            </Button>
                        </div>
                    </div>
                </main>
            </div>
        </div>
    )
}

export default function SignIn() {
    return (
        <RenderPage/>
    )
}