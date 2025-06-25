import {Avatar, Box, Button, Divider, Grid, Paper, Stack, InputAdornment, IconButton, TextField, Typography} from "@mui/material";
import {useState} from "react";
import Visibility from "@mui/icons-material/Visibility";
import VisibilityOff from "@mui/icons-material/VisibilityOff";

export default function Password() {

    const [showPassword, setShowPassword] = useState({
        current: false,
        new: false,
        confirm: false,
    });

    const handleClickShowPassword = (field) => {
        setShowPassword({ ...showPassword, [field]: !showPassword[field] });
    };


    return (
        <Box>
            <Typography variant="h6" fontWeight={700}>
                Password Settings
            </Typography>
            <Typography sx={{ color: "text.secondary", mb: 3 }}>
                Please fill full details about yourself
            </Typography>

            <Stack spacing={3}>
                <TextField
                    fullWidth
                    label="Current Password"
                    type={showPassword.current ? "text" : "password"}
                    InputProps={{
                        endAdornment: (
                            <InputAdornment position="end">
                                <IconButton onClick={() => handleClickShowPassword("current")}>
                                    {showPassword.current ? <Visibility /> : <VisibilityOff />}
                                </IconButton>
                            </InputAdornment>
                        ),
                    }}
                />

                <TextField
                    fullWidth
                    label="New Password"
                    type={showPassword.new ? "text" : "password"}
                    InputProps={{
                        endAdornment: (
                            <InputAdornment position="end">
                                <IconButton onClick={() => handleClickShowPassword("new")}>
                                    {showPassword.new ? <Visibility /> : <VisibilityOff />}
                                </IconButton>
                            </InputAdornment>
                        ),
                    }}
                />

                <TextField
                    fullWidth
                    label="Confirm Password"
                    type={showPassword.confirm ? "text" : "password"}
                    InputProps={{
                        endAdornment: (
                            <InputAdornment position="end">
                                <IconButton onClick={() => handleClickShowPassword("confirm")}>
                                    {showPassword.confirm ? <Visibility /> : <VisibilityOff />}
                                </IconButton>
                            </InputAdornment>
                        ),
                    }}
                />

                {/* Password Requirements */}
                <Box>
                    <Typography variant="subtitle2" fontWeight={600}>
                        Password Requirements:
                    </Typography>
                    <ul style={{ marginTop: 4, paddingLeft: 16, color: "#555" }}>
                        <li>At least one lowercase character</li>
                        <li>Minimum 8 characters long - the more, the better</li>
                        <li style={{ color: "#999" }}>
                            At least one number, symbol, or whitespace character
                        </li>
                    </ul>
                </Box>



                {/* Actions */}
                <Box display="flex" justifyContent="flex-end" gap={2}>
                    <Button variant="outlined" color="primary">
                        Cancel
                    </Button>
                    <Button variant="contained" color="primary">
                        Save Changes
                    </Button>
                </Box>
            </Stack>
        </Box>



    );
}