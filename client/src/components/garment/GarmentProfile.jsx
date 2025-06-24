import React from "react";
import {
    Box,
    Card,
    CardContent,
    Avatar,
    Typography,
    Button,
    Grid,
    Stack,
    Divider,
    LinearProgress,
    List,
    ListItem,
    ListItemIcon,
    ListItemText,
    Chip
} from "@mui/material";
import PublicIcon from "@mui/icons-material/Public";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import WorkOutlineIcon from "@mui/icons-material/WorkOutline";
import LanguageIcon from "@mui/icons-material/Language";
import AccessTimeIcon from "@mui/icons-material/AccessTime";
import RemoveRedEyeIcon from "@mui/icons-material/RemoveRedEye";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import InfoOutlinedIcon from "@mui/icons-material/InfoOutlined";
import StarRateIcon from "@mui/icons-material/StarRate";
import AddCircleOutlineIcon from "@mui/icons-material/AddCircleOutline";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import { Link as RouterLink } from "react-router-dom";

// ---- Dummy data ----
const profile = {
    name: "Your UniSew Name",
    username: "@trieumn",
    location: "Vietnam",
    joined: "June 2025",
    industry: "",
    languages: "",
    workingHours: "",
};

const checklist = [
    {
        icon: <InfoOutlinedIcon color="primary" />,
        title: "Share how you plan to use UniSew",
        desc: "Tell us if you’re here to find services or offer them.",
        percent: 75,
        done: false
    },
    {
        icon: <AddCircleOutlineIcon color="action" />,
        title: "Add details for your profile",
        desc: "Upload a photo and info for a more tailored experience.",
        action: "Add"
    },
    {
        icon: <WorkOutlineIcon color="action" />,
        title: "Tell us about your business",
        desc: "Get tailored recommendations and tips to help it grow.",
        action: "Add"
    },
    {
        icon: <AccessTimeIcon color="action" />,
        title: "Set your communication preferences",
        desc: "Let freelancers know your collaboration preferences.",
        action: "Add"
    }
];

const LinkBehavior = React.forwardRef((props, ref) =>
    <RouterLink ref={ref} {...props} />
);

// ---- Components ----

function ProfileCard({ profile }) {
    return (
        <Card sx={{ borderRadius: 3, textAlign: "center", px: 2 }}>
            <CardContent>
                <Avatar sx={{ width: 70, height: 70, mx: "auto", mb: 2, fontSize: 34, bgcolor: "#e0e0e0", color: "#757575" }}>
                    T
                </Avatar>
                <Typography variant="h6" fontWeight="bold">{profile.name}</Typography>
                <Typography variant="body2" color="text.secondary" mb={2}>{profile.username}</Typography>
                <Divider sx={{ my: 1.5 }} />
                <Stack spacing={1} sx={{ textAlign: "left", pl: 2, mb: 2 }}>
                    <Stack direction="row" spacing={1} alignItems="center">
                        <PublicIcon fontSize="small" color="action" />
                        <Typography variant="body2">Located in {profile.location}</Typography>
                    </Stack>
                    <Stack direction="row" spacing={1} alignItems="center">
                        <CalendarMonthIcon fontSize="small" color="action" />
                        <Typography variant="body2">Joined in {profile.joined}</Typography>
                    </Stack>
                    <Stack direction="row" spacing={1} alignItems="center">
                        <WorkOutlineIcon fontSize="small" color="action" />
                        <Typography variant="body2">{profile.industry || "Your industry"}</Typography>
                    </Stack>
                    <Stack direction="row" spacing={1} alignItems="center">
                        <LanguageIcon fontSize="small" color="action" />
                        <Typography variant="body2">{profile.languages || "Preferred languages"}</Typography>
                    </Stack>
                    <Stack direction="row" spacing={1} alignItems="center">
                        <AccessTimeIcon fontSize="small" color="action" />
                        <Typography variant="body2">{profile.workingHours || "Preferred working hours"}</Typography>
                    </Stack>
                </Stack>
                <Button
                    variant="outlined"
                    fullWidth
                    startIcon={<RemoveRedEyeIcon />}
                    sx={{ mb: 1, borderRadius: 2, textTransform: "none", fontWeight: "bold" }}
                    component={LinkBehavior}
                    to="/designer/list"
                >
                    Preview public profile
                </Button>
                <Button
                    variant="outlined"
                    fullWidth
                    endIcon={<ArrowForwardIcon />}
                    sx={{ borderRadius: 2, textTransform: "none", fontWeight: "bold" }}
                    component={LinkBehavior}
                    to="/designer/list"
                >
                    Explore UniSew
                </Button>
                <Typography
                    variant="caption"
                    color="text.secondary"
                    display="block"
                    mt={2}
                >
                    You're currently on your buyer profile. To access your freelancer profile, switch to seller mode
                </Typography>
            </CardContent>
        </Card>
    );
}

function ProfileBreadcrumb() {
    return (
        <Box sx={{ maxWidth: 980, mx: "auto", mb: 1 }}>
            <Typography variant="body2" color="text.secondary">
                Home / My Profile
            </Typography>
        </Box>
    );
}

function ProfileHeadline() {
    return (
        <Box sx={{ mb: 2 }}>
            <Typography variant="h5" fontWeight="bold" mb={1}>
                Hi <span role="img" aria-label="wave">👋</span> Let’s help freelancers get to know you
            </Typography>
            <Typography variant="body2" color="text.secondary">
                Get the most out of UniSew by sharing a bit more about yourself and how you prefer to work with freelancers.
            </Typography>
        </Box>
    );
}

function ProfileChecklist({ checklist }) {
    return (
        <Card sx={{ mb: 3, borderRadius: 3 }}>
            <CardContent>
                <Typography variant="subtitle1" fontWeight="bold" mb={1.5}>
                    Profile checklist
                </Typography>
                <LinearProgress
                    variant="determinate"
                    value={25}
                    sx={{ height: 7, borderRadius: 5, mb: 2, bgcolor: "#e0e8ef" }}
                />
                <List disablePadding>
                    {checklist.map((item, i) => (
                        <ListItem key={i} disablePadding sx={{ alignItems: "flex-start", mb: 1.5 }}>
                            <ListItemIcon sx={{ mt: 0.5 }}>{item.icon}</ListItemIcon>
                            <ListItemText
                                primary={
                                    <Stack direction="row" spacing={1} alignItems="center">
                                        <Typography variant="body1" fontWeight="bold">
                                            {item.title}
                                        </Typography>
                                        {item.percent && (
                                            <Chip
                                                label={`${item.percent}%`}
                                                color={item.percent === 100 ? "success" : "primary"}
                                                size="small"
                                                sx={{ fontWeight: 600 }}
                                            />
                                        )}
                                    </Stack>
                                }
                                secondary={item.desc}
                                secondaryTypographyProps={{ color: "text.secondary" }}
                            />
                            <Box>
                                {item.percent === 75 ? (
                                    <Typography variant="body2" color="primary" fontWeight="bold">75%</Typography>
                                ) : (
                                    <Button
                                        size="small"
                                        sx={{ textTransform: "none", fontWeight: 600, color: "#3488e2" }}
                                        endIcon={<ChevronRightIcon fontSize="small" />}
                                    >
                                        {item.action}
                                    </Button>
                                )}
                            </Box>
                        </ListItem>
                    ))}
                </List>
            </CardContent>
        </Card>
    );
}

function ProfileReviews() {
    return (
        <Card sx={{ borderRadius: 3, textAlign: "center" }}>
            <CardContent>
                <Typography variant="subtitle1" fontWeight="bold" mb={2}>
                    Reviews from freelancers
                </Typography>
                <Box sx={{ display: "flex", flexDirection: "column", alignItems: "center" }}>
                    <Stack direction="row" spacing={0.3} mb={1.5}>
                        {[...Array(5)].map((_, i) => (
                            <StarRateIcon key={i} color="warning" />
                        ))}
                    </Stack>
                    <Typography variant="body2" color="text.secondary">
                        trieumn doesn't have any reviews yet.
                    </Typography>
                </Box>
            </CardContent>
        </Card>
    );
}

// ---- Main Component ----

export default function GarmentProfile() {
    return (
        <Box sx={{ bgcolor: "#f6fafd", minHeight: "100vh", py: 5 }}>
            <Box sx={{ maxWidth: 1200, mx: "auto" }}>
                <Grid container spacing={3}>
                    {/* Left: Card info */}
                    <Grid item xs={12} md={4} maxWidth={400}>
                        <ProfileCard profile={profile} />
                    </Grid>
                    {/* Right: Checklist & Review */}
                    <Grid item xs={12} md={8}>
                        <ProfileBreadcrumb />
                        <ProfileHeadline />
                        <ProfileChecklist checklist={checklist} />
                        <ProfileReviews />
                    </Grid>
                </Grid>
            </Box>
        </Box>
    );
}
