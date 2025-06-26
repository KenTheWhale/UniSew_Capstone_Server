import {Avatar, Box, Button, Divider, Grid, Paper, Stack, styled, TextField, Typography} from "@mui/material";
import {useRef, useState} from "react";
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
    const [avatarUrl, setAvatarUrl] = useState("https://i.pravatar.cc/100");
    const [isDragOver, setIsDragOver] = useState(false);
    const fileInputRef = useRef(null);

    // Handle file selection (both click and drag)
    const handleFileSelect = (file) => {
        if (file && file.type.startsWith('image/')) {
            const reader = new FileReader();
            reader.onload = (e) => {
                setAvatarUrl(e.target.result);
            };
            reader.readAsDataURL(file);
        } else {
            alert('Please select a valid image file (SVG, PNG, JPEG, or GIF)');
        }
    };

    // Handle drag over
    const handleDragOver = (e) => {
        e.preventDefault();
        setIsDragOver(true);
    };

    // Handle drag leave
    const handleDragLeave = (e) => {
        e.preventDefault();
        setIsDragOver(false);
    };

    // Handle drop
    const handleDrop = (e) => {
        e.preventDefault();
        setIsDragOver(false);

        const files = e.dataTransfer.files;
        if (files.length > 0) {
            handleFileSelect(files[0]);
        }
    };

    // Handle click to upload
    const handleClick = () => {
        fileInputRef.current?.click();
    };

    // Handle file input change
    const handleFileInputChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            handleFileSelect(file);
        }
    };

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
                <Stack spacing={2} direction={{ xs: "column", sm: "row" }} alignItems="center">
                    <Avatar
                        src={avatarUrl}
                        sx={{ width: 64, height: 64, flexShrink: 0 }}
                    />

                    {/* DropZone (right side) */}
                    <DropZone
                        isDragOver={isDragOver}
                        onDragOver={handleDragOver}
                        onDragLeave={handleDragLeave}
                        onDrop={handleDrop}
                        onClick={handleClick}
                    >
                        <CloudUploadIcon fontSize="large" />
                        <Typography>Click to upload or drag and drop</Typography>
                        <Typography variant="caption" color="primary">
                            SVG, PNG, JPEG OR GIF (max 1080x1200px)
                        </Typography>
                    </DropZone>

                    {/* Hidden file input */}
                    <input
                        type="file"
                        ref={fileInputRef}
                        onChange={handleFileInputChange}
                        accept="image/*"
                        style={{ display: 'none' }}
                    />
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

