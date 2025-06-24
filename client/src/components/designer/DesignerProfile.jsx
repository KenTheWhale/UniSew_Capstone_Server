import {Avatar, Box, Button, Chip, Paper, Stack, Typography} from "@mui/material";
import LocationOnIcon from '@mui/icons-material/LocationOn';
import BrushIcon from '@mui/icons-material/Brush';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import { useState} from "react";
import MyDetails from "./components/MyDetails.jsx";
import MyFeedback from "./components/MyFeedback.jsx";
import Billing from "./components/Billing.jsx";
import Password from "./components/Password.jsx";
import Plan from "./components/Plan.jsx";



function DesignerProfile() {
    const [coverImage, setCoverImage] = useState("default-image-url");
    const [activeTab, setActiveTab] = useState("My Details");
    const handleCoverUpload = (e) => {
        const file = e.target.files[0];
        if (file) {
            const newUrl = URL.createObjectURL(file);
            setCoverImage(newUrl);
        }
    };
    const renderTabComponent = () => {
        switch (activeTab) {
            case "My Details":
                return <MyDetails />;
            case "My Feedback":
                return <MyFeedback />;
            case "Password":
                return <Password />;
            case "Billing":
                return <Billing />;
            case "Plan":
                return <Plan />;
            default:
                return null;
        }
    };


    return (
        <>
            <Box>
                <Paper elevation={3} sx={{
                    borderRadius: 3,
                    overflow: 'hidden',
                    bgcolor: '#f1f6fd',
                }}>
                    {/* Cover Section */}
                    <Box sx={{ position: 'relative', height: 200, bgcolor: 'primary.dark' }}>
                        <Box
                            component="img"
                            src={coverImage}
                            alt="Cover"
                            sx={{
                                width: '100%',
                                height: '100%',
                                objectFit: 'cover',
                                filter: 'brightness(0.8)',
                            }}
                        />
                        <Button
                            component="label"
                            variant="outlined"
                            size="small"
                            sx={{
                                position: 'absolute',
                                top: 16,
                                right: 16,
                                borderRadius: '20px',
                                color: 'white',
                                borderColor: 'white',
                                textTransform: 'none',
                                backgroundColor: 'rgba(255,255,255,0.1)',
                                '&:hover': {
                                    backgroundColor: 'rgba(255,255,255,0.2)',
                                },
                            }}
                        >
                            Edit Cover
                            <input
                                type="file"
                                accept="image/*"
                                hidden
                                onChange={handleCoverUpload}
                            />
                        </Button>
                        {/* Avatar */}
                        <Avatar
                            alt="User"
                            src="#"
                            sx={{
                                width: 100,
                                height: 100,
                                border: '4px solid white',
                                position: 'absolute',
                                bottom: -50,
                                left: 30,
                            }}
                        />
                    </Box>

                    {/* Profile Info */}
                    <Box sx={{ px: 3, pt: 7, pb: 2, bgcolor: 'white' }}>
                        <Typography variant="h6" fontWeight={700}>
                            Mohid Khan
                        </Typography>

                        <Stack
                            direction="row"
                            spacing={2}
                            alignItems="center"
                            sx={{ color: 'text.secondary', mt: 0.5 }}
                        >
                            <Stack direction="row" spacing={0.5} alignItems="center">
                                <BrushIcon sx={{ fontSize: 16 }} />
                                <Typography variant="body2">UX Designer</Typography>
                            </Stack>

                            <Stack direction="row" spacing={0.5} alignItems="center">
                                <LocationOnIcon sx={{ fontSize: 16 }} />
                                <Typography variant="body2">Sans Francisco</Typography>
                            </Stack>

                            <Stack direction="row" spacing={0.5} alignItems="center">
                                <CalendarTodayIcon sx={{ fontSize: 16 }} />
                                <Typography variant="body2">Join August 2024</Typography>
                            </Stack>
                        </Stack>
                    </Box>

{/*                    <Stack
                        direction="row"
                        spacing={3}
                        sx={{
                            px: 3,
                            pt: 1,
                            pb: 1,
                            bgcolor: '#f1f6fd',
                            borderTop: '1px solid #e0e0e0',
                            borderBottom: '1px solid #e0e0e0',
                        }}
                    >
                        <Button
                            variant="text"
                            sx={{
                                textTransform: 'none',
                                fontWeight: 600,
                                color: '#1976d2',
                                borderBottom: '2px solid #1976d2',
                                borderRadius: 0,
                            }}
                        >
                            My Details
                        </Button>
                        <Button
                            variant="text"
                            sx={{
                                textTransform: 'none',
                                fontWeight: 500,
                                color: 'text.secondary',
                                borderRadius: 0,
                                '&:hover': {
                                    borderBottom: '2px solid #90caf9',
                                },
                            }}
                        >
                            My Feedback
                        </Button>

                        <Button
                            variant="text"
                            sx={{
                                textTransform: 'none',
                                fontWeight: 500,
                                color: 'text.secondary',
                                borderRadius: 0,
                                '&:hover': {
                                    borderBottom: '2px solid #90caf9',
                                },
                            }}
                        >
                            Password
                        </Button>
                        <Button
                            variant="text"
                            sx={{
                                textTransform: 'none',
                                fontWeight: 500,
                                color: 'text.secondary',
                                borderRadius: 0,
                                '&:hover': {
                                    borderBottom: '2px solid #90caf9',
                                },
                            }}
                        >
                            Plan
                        </Button>
                        <Button
                            variant="text"
                            sx={{
                                textTransform: 'none',
                                fontWeight: 500,
                                color: 'text.secondary',
                                borderRadius: 0,
                                '&:hover': {
                                    borderBottom: '2px solid #90caf9',
                                },
                            }}
                        >
                            Billing
                        </Button>
                    </Stack>*/}

                    {/* Tab Header */}
                    <Stack direction="row" spacing={3} sx={{ px: 3, py: 1, borderTop: '1px solid #eee', borderBottom: '1px solid #eee' }}>
                        {["My Details", "My Feedback", "Password", "Billing", "Plan"].map(tab => (
                            <Button
                                key={tab}
                                onClick={() => setActiveTab(tab)}
                                sx={{
                                    textTransform: 'none',
                                    fontWeight: activeTab === tab ? 600 : 400,
                                    color: activeTab === tab ? '#1976d2' : 'text.secondary',
                                    borderBottom: activeTab === tab ? '2px solid #1976d2' : '2px solid transparent',
                                    borderRadius: 0
                                }}
                            >
                                {tab}
                            </Button>
                        ))}
                    </Stack>

                    {/* Tab Content */}

                </Paper>
                <Paper elevation={3} sx={{
                    borderRadius: 3,
                    overflow: 'hidden',
                    bgcolor: '#f1f6fd',
                    marginTop: 5,
                }}>
                    <Box  sx={{ px: 3, py: 2, bgcolor: 'white' }}>
                        {renderTabComponent()}
                    </Box>
                </Paper>
            </Box>


        </>
    )
}





export default DesignerProfile;