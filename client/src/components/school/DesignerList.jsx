import React, {useEffect, useState} from 'react';
import { Box, Typography, Grid, Card, CardContent, CardMedia, Button, Avatar } from '@mui/material';
import WebAppUILayout from "../../layouts/ui/WebAppUILayout.jsx";
import {viewListDesigner} from "../../services/DesignerService.jsx";
import {useNavigate} from "react-router-dom";

const DesignerList = () => {
    const [designers, setDesigners] = useState([]);
    const navigate = useNavigate();
    const fetchDesigners = async () => {
        try {
            const res = await viewListDesigner();

            setDesigners(res.data);
        } catch (error) {
            console.error("Failed to fetch designers:", error);
        }
    };


    const handleViewProfile = (designer) => {
        navigate("/designer/detail", {
            state: { designer }
        });
    };

    useEffect(() => {
        fetchDesigners();
    }, []);
    return (
        <WebAppUILayout title="Designers">
        <Box p={4}>
            <Typography variant="h4" gutterBottom fontWeight={700}>
                DESIGNERS
            </Typography>
            <Grid container spacing={3}>
                {designers.map((designer, index) => {
                    const basicPackage = designer.package?.find(pkg => pkg.name === "Basic Design");

                    return (
                        <Grid item xs={12} sm={6} md={4} key={index}>
                            <Card sx={{ borderRadius: 2, boxShadow: 3 }}>
                                <CardMedia
                                    component="img"
                                    height="180"
                                    image={designer.profile.avatar}
                                    alt={designer.profile.name}
                                />
                                <CardContent>
                                    <Box display="flex" alignItems="center" gap={1} mb={1}>
                                        <Avatar src={designer.profile.avatar} alt={designer.profile.name} />
                                        <Typography variant="subtitle1" fontWeight="bold">
                                            {designer.profile.name}
                                        </Typography>
                                    </Box>
                                    <Typography variant="body2" color="text.secondary">
                                        {designer.bio}
                                    </Typography>
                                    <Typography variant="body2" mt={1}>
                                        ⭐ 4.9
                                    </Typography>
                                    <Typography variant="body2" mt={1} fontWeight="bold">
                                        From ${basicPackage?.fee || 0}
                                    </Typography>
                                    <Button
                                        variant="outlined"
                                        size="small"
                                        fullWidth
                                        sx={{ mt: 1 }}
                                        onClick={() => handleViewProfile(designer)}
                                    >
                                        View Profile
                                    </Button>
                                </CardContent>
                            </Card>
                        </Grid>
                    );
                })}
            </Grid>
        </Box>
        </WebAppUILayout>
    );
};

export default DesignerList;