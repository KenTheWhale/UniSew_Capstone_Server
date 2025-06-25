import {Avatar, Box, Button, Divider, Grid, Paper, Stack, styled, TextField, Typography} from "@mui/material";
import {useState} from "react";
import CloudUploadIcon from '@mui/icons-material/CloudUpload';

const DropZone = styled(Box)(({ theme }) => ({
    border: `2px dashed ${theme.palette.primary.light}`,
    backgroundColor: theme.palette.primary.light + "11",
    borderRadius: 12,
    textAlign: "center",
    color: theme.palette.text.secondary,
    width: '100%',     // 💡 make sure it stretches
    padding: `0 ${theme.spacing(59)}`,
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',
}));


export default function MyDetails() {
    const [bio, setBio] = useState(
        "I'm a Product Designer based in Melbourne, Australia..."
    );

    return (
        <Paper elevation={0} sx={{ borderRadius: 3, p: 3 }}>
            <Typography variant="h6" fontWeight="bold" gutterBottom>
                My Details
            </Typography>
            <Typography variant="body2" color="text.secondary" mb={3}>
                Please fill full details about yourself
            </Typography>

            <Grid container spacing={3}  alignItems="center">
                {/* First Name - Last Name */}

                    <TextField fullWidth label="First Name" placeholder="Enter First Name" />
                    <TextField fullWidth label="Last Name" placeholder="Enter Last Name" />
                {/* Email - Phone Number */}
                <TextField fullWidth label="Email" placeholder="Enter Email" />
                <TextField fullWidth label="Phone Number" placeholder="Enter Phone Number" />


                {/* Avatar - Upload */}
                <Stack spacing={2} direction={"row"}>
                    <Avatar
                        src="https://i.pravatar.cc/100"
                        sx={{ width: 64, height: 64 }}
                    />

                    {/* DropZone (right side) */}
                    <DropZone>
                        <CloudUploadIcon fontSize="large" />
                        <Typography>Click to upload or drag and drop</Typography>
                        <Typography variant="caption" color="primary">
                            SVG, PNG, JPEG OR GIF (max 1080x1200px)
                        </Typography>
                    </DropZone>
                </Stack>





                {/* Role - ZIP Code */}
                <TextField fullWidth label="Role" value="Designer" aria-readonly={true}/>
                <TextField fullWidth label="ZIP Code" placeholder="Enter ZIP Code" />


                {/* Bio (full width) */}

                    <TextField
                        fullWidth
                        multiline
                        minRows={4}
                        label="Bio"
                        value={bio}
                        onChange={(e) => setBio(e.target.value)}
                    />

            </Grid>

            {/* Action Buttons */}
            <Stack direction="row" justifyContent="flex-end" spacing={2} mt={4}>
                <Button variant="outlined" color="secondary">
                    Cancel
                </Button>
                <Button variant="contained" color="primary">
                    Save Changes
                </Button>
            </Stack>
        </Paper>
    );
}