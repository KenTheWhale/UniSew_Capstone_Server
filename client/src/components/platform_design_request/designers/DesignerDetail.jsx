import {
    AppBar,
    Avatar,
    Box,
    Button,
    Card, CardActions,
    CardContent, CardMedia, Chip,
    Divider,
    Grid, IconButton,
    LinearProgress,
    ListItem, Paper, Rating, List,
    Stack, TextField, Toolbar,
    Typography, Pagination, Fab, Tooltip, styled, DialogTitle, Dialog, DialogContent, DialogActions
} from "@mui/material";
import MyLocationIcon from '@mui/icons-material/MyLocation';
import WorkIcon from '@mui/icons-material/Work';
import Container from '@mui/material/Container';
import FavoriteIcon from '@mui/icons-material/Favorite';
import ShareIcon from '@mui/icons-material/Share';
import MailIcon from '@mui/icons-material/Mail';
import PhoneIcon from '@mui/icons-material/Phone';
import ReviewsIcon from '@mui/icons-material/Reviews';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import VerifiedIcon from '@mui/icons-material/Verified';
import ThumbUpIcon from '@mui/icons-material/ThumbUp';
import ReplyIcon from '@mui/icons-material/Reply';
import CreateIcon from '@mui/icons-material/Create';
import EditIcon from '@mui/icons-material/Edit'
import CloseIcon from '@mui/icons-material/Close';
import {useState} from "react";
import PhotoCameraIcon from '@mui/icons-material/PhotoCamera';


const Item = styled(Paper)(({ theme }) => ({
    backgroundColor: 'transparent',
    boxShadow: 'none',
    padding: theme.spacing(1),
}));




const DesignerDetail = () => {

    {/* Portfolio data */}
    const portfolio = [
        { id: 1, image: "https://images.unsplash.com/photo-1561070791-2526d30994b5?w=300&h=300&fit=crop", title: "Tech Startup Logo" },
        { id: 2, image: "https://images.unsplash.com/photo-1551650975-87deedd944c3?w=300&h=300&fit=crop", title: "E-commerce Website" },
        { id: 3, image: "https://images.unsplash.com/photo-1611224923853-80b023f02d71?w=300&h=300&fit=crop", title: "Social Campaign" },
        { id: 4, image: "https://images.unsplash.com/photo-1586953208448-b95a79798f07?w=300&h=300&fit=crop", title: "Package Design" },
        { id: 5, image: "https://images.unsplash.com/photo-1541961017774-22349e4a1262?w=300&h=300&fit=crop", title: "Digital Illustration" },
        { id: 6, image: "https://images.unsplash.com/photo-1572044162444-ad60f128bdea?w=300&h=300&fit=crop", title: "Brand Identity" }
    ];

    {/* Services data */}
    const services = [
        {
            id: 1,
            title: "Logo Design & Cloth Design",
            image: "https://images.unsplash.com/photo-1561070791-2526d30994b5?w=400&h=300&fit=crop",
            price: "Starting at $25",
            rating: 4.9,
            reviews: 847,
            deliveryTime: "2 days",
            tags: ["Logo", "Branding", "Identity"]
        },
        {
            id: 2,
            title: "Logo Design & Cloth Design",
            image: "https://images.unsplash.com/photo-1561070791-2526d30994b5?w=400&h=300&fit=crop",
            price: "Starting at $25",
            rating: 4.9,
            reviews: 847,
            deliveryTime: "2 days",
            tags: ["Logo", "Branding", "Identity"]
        },
        {
            id: 3,
            title: "Logo Design & Cloth Design",
            image: "https://images.unsplash.com/photo-1561070791-2526d30994b5?w=400&h=300&fit=crop",
            price: "Starting at $25",
            rating: 4.9,
            reviews: 847,
            deliveryTime: "2 days",
            tags: ["Logo", "Branding", "Identity"]
        },
        {
            id: 4,
            title: "Logo Design & Cloth Design",
            image: "https://images.unsplash.com/photo-1561070791-2526d30994b5?w=400&h=300&fit=crop",
            price: "Starting at $25",
            rating: 4.9,
            reviews: 847,
            deliveryTime: "2 days",
            tags: ["Logo", "Branding", "Identity"]
        },
        {
            id: 5,
            title: "Logo Design & Cloth Design",
            image: "https://images.unsplash.com/photo-1561070791-2526d30994b5?w=400&h=300&fit=crop",
            price: "Starting at $25",
            rating: 4.9,
            reviews: 847,
            deliveryTime: "2 days",
            tags: ["Logo", "Branding", "Identity"]
        },
        {
            id: 6,
            title: "Logo Design & Cloth Design",
            image: "https://images.unsplash.com/photo-1561070791-2526d30994b5?w=400&h=300&fit=crop",
            price: "Starting at $25",
            rating: 4.9,
            reviews: 847,
            deliveryTime: "2 days",
            tags: ["Logo", "Branding", "Identity"]
        }
    ];
    {/* Feedback data */}

    const reviewDataList = [
        {
            id: 1,
            user: {
                name: "Sarah Johnson",
                avatar: "https://images.unsplash.com/photo-1494790108755-2616b612b786?w=150&h=150&fit=crop&crop=face",
                isVerified: true,
                location: "New York, USA"
            },
            rating: 4.5,
            date: "2 weeks ago",
            content: "Absolutely amazing work! The designer exceeded my expectations and delivered exactly what I was looking for. The attention to detail is incredible and the communication throughout the project was excellent. I'll definitely be working with them again!",
            images: [
                "https://images.unsplash.com/photo-1561070791-2526d30994b5?w=300&h=200&fit=crop",
                "https://images.unsplash.com/photo-1551650975-87deedd944c3?w=300&h=200&fit=crop",
                "https://images.unsplash.com/photo-1611224923853-80b023f02d71?w=300&h=200&fit=crop"
            ],
            likes: 12,
            helpful: true,
            projectType: "Logo Design"
        },
        {
            id: 2,
            user: {
                name: "John Cena",
                avatar: "https://images.unsplash.com/photo-1494790108755-2616b612b786?w=150&h=150&fit=crop&crop=face",
                isVerified: true,
                location: "New York, USA"
            },
            rating: 4.5,
            date: "2 weeks ago",
            content: "Absolutely amazing work! The designer exceeded my expectations and delivered exactly what I was looking for. The attention to detail is incredible and the communication throughout the project was excellent. I'll definitely be working with them again!",
            images: [
                "https://images.unsplash.com/photo-1561070791-2526d30994b5?w=300&h=200&fit=crop",
                "https://images.unsplash.com/photo-1551650975-87deedd944c3?w=300&h=200&fit=crop",
                "https://images.unsplash.com/photo-1611224923853-80b023f02d71?w=300&h=200&fit=crop"
            ],
            likes: 12,
            helpful: true,
            projectType: "Logo Design"
        },
        {
            id: 3,
            user: {
                name: "John Conor",
                avatar: "https://images.unsplash.com/photo-1494790108755-2616b612b786?w=150&h=150&fit=crop&crop=face",
                isVerified: true,
                location: "New York, USA"
            },
            rating: 4.5,
            date: "2 weeks ago",
            content: "Absolutely amazing work! The designer exceeded my expectations and delivered exactly what I was looking for. The attention to detail is incredible and the communication throughout the project was excellent. I'll definitely be working with them again!",
            images: [
                "https://images.unsplash.com/photo-1561070791-2526d30994b5?w=300&h=200&fit=crop",
                "https://images.unsplash.com/photo-1551650975-87deedd944c3?w=300&h=200&fit=crop",
                "https://images.unsplash.com/photo-1611224923853-80b023f02d71?w=300&h=200&fit=crop"
            ],
            likes: 12,
            helpful: true,
            projectType: "Logo Design"
        },
    ];
    {/* handle pagination Section */}
    const reviewsPerPage = 2;
    const [page, setPage] = useState(1);

    const handleChangePage = (event, value) => {
        setPage(value);
    };

    const paginatedReviews = reviewDataList.slice(
        (page - 1) * reviewsPerPage,
        page * reviewsPerPage
    );



    {/* handle feedback dialog Section */}
    const [open, setOpen] = useState(false);
    const [name, setName] = useState('');
    const [rating, setRating] = useState(0);
    const [review, setReview] = useState('');
    const [images, setImages] = useState([]);

    const handleOpen = () => setOpen(true);
    const handleClose = () => {
        setOpen(false);
        setName('');
        setRating(0);
        setReview('');
        setImages([]);
    };

    const handleImageChange = (e) => {
        const files = Array.from(e.target.files);
        setImages(files);
    };

    const handleSubmit = () => {
        const feedbackData = {
            name,
            rating,
            review,
            images
        };

        console.log("Feedback submitted:", feedbackData);
        // You can send this to server or update state
        handleClose();
    };




    return (

        <Box sx={{ flexGrow: 1 }}>

            {/* Hero Section */}
            <Box sx={{
                background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                color: 'white',
                py: 8,
                textAlign: 'center'
            }}>
                <Container maxWidth="md">
                    <Avatar
                        sx={{
                            width: 120,
                            height: 120,
                            mx: 'auto',
                            mb: 3,
                            border: '4px solid white'
                        }}
                        src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face"
                    />
                    <Typography variant="h3" component="h1" gutterBottom sx={{ fontWeight: 'bold' }}>
                        Alex Johnson
                    </Typography>
                    <Typography variant="h5" sx={{ mb: 3, opacity: 0.9 }}>
                        Professional Graphic Designer & Tailor Specialist
                    </Typography>
                    <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', mb: 3 }}>
                        <Rating value={4.9} precision={0.1} readOnly sx={{ color: '#ffc107', mr: 1 }} />
                        <Typography variant="body1">4.9 (2,847 reviews)</Typography>
                    </Box>
                    <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mb: 4 }}>
                        <Chip icon={<MyLocationIcon />} label="New York, USA" sx={{ bgcolor: 'rgba(255,255,255,0.2)', color: 'white' }} />
                        <Chip icon={<WorkIcon />} label="5+ Years Experience" sx={{ bgcolor: 'rgba(255,255,255,0.2)', color: 'white' }} />
                    </Box>
                    <Button variant="contained" size="large" sx={{ bgcolor: 'white', color: '#667eea', '&:hover': { bgcolor: '#f5f5f5' } }}>
                        Quote 
                    </Button>
                </Container>
            </Box>
            {/* Services Section */}
            <Container maxWidth="lg" sx={{ py: 6 }}>
                <Typography variant="h4" component="h2" gutterBottom sx={{ fontWeight: 'bold', mb: 4 }}>
                    My Services
                </Typography>
                <Grid container spacing={3}>
                    {services.map((service) => (
                        <Grid item xs={12} sm={6} md={4} key={service.id}>
                            <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column', '&:hover': { transform: 'translateY(-4px)', transition: 'transform 0.3s' } }}>
                                <CardMedia
                                    component="img"
                                    height="200"
                                    image={service.image}
                                    alt={service.title}
                                />
                                <CardContent sx={{ flexGrow: 1 }}>
                                    <Typography gutterBottom variant="h6" component="h3" sx={{ fontWeight: 'bold' }}>
                                        {service.title}
                                    </Typography>
                                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                                        <Rating value={service.rating} precision={0.1} readOnly size="small" />
                                        <Typography variant="body2" sx={{ ml: 1 }}>
                                            {service.rating} ({service.reviews})
                                        </Typography>
                                    </Box>
                                    <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5, mb: 2 }}>
                                        {service.tags.map((tag, index) => (
                                            <Chip key={index} label={tag} size="small" variant="outlined" />
                                        ))}
                                    </Box>
                                    <Typography variant="body2" color="text.secondary">
                                        Delivery: {service.deliveryTime}
                                    </Typography>
                                </CardContent>
                                <CardActions sx={{ justifyContent: 'space-between', px: 2, pb: 2 }}>
                                    <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#1976d2' }}>
                                        {service.price}
                                    </Typography>
                                    <Box>
                                        <IconButton size="small">
                                            <FavoriteIcon />
                                        </IconButton>
                                        <IconButton size="small">
                                            <ShareIcon />
                                        </IconButton>
                                    </Box>
                                </CardActions>
                            </Card>
                        </Grid>
                    ))}
                </Grid>
            </Container>
            {/* Portfolio Section */}
            <Box sx={{ bgcolor: '#f5f5f5', py: 6 }}>
                <Container maxWidth="lg">
                    <Typography variant="h4" component="h2" gutterBottom sx={{ fontWeight: 'bold', mb: 4 }}>
                        Portfolio
                    </Typography>
                    <Grid container spacing={2}>
                        {portfolio.map((item) => (
                            <Grid item xs={12} sm={6} md={4} key={item.id}>
                                <Paper
                                    sx={{
                                        position: 'relative',
                                        overflow: 'hidden',
                                        '&:hover': {
                                            '& .overlay': { opacity: 1 },
                                            transform: 'scale(1.05)',
                                            transition: 'all 0.3s'
                                        }
                                    }}
                                >
                                    <Box
                                        component="img"
                                        src={item.image}
                                        alt={item.title}
                                        sx={{ width: '100%', height: 250, objectFit: 'cover' }}
                                    />
                                    <Box
                                        className="overlay"
                                        sx={{
                                            position: 'absolute',
                                            top: 0,
                                            left: 0,
                                            right: 0,
                                            bottom: 0,
                                            bgcolor: 'rgba(0,0,0,0.7)',
                                            color: 'white',
                                            display: 'flex',
                                            alignItems: 'center',
                                            justifyContent: 'center',
                                            opacity: 0,
                                            transition: 'opacity 0.3s'
                                        }}
                                    >
                                        <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                                            {item.title}
                                        </Typography>
                                    </Box>
                                </Paper>
                            </Grid>
                        ))}
                    </Grid>
                </Container>
            </Box>



            {/* Review Section */}
            {/*<Card
                        sx={{
                            width: "100%",
                            maxWidth: 600,
                            margin: "0 auto",
                            backgroundColor: "#FFEAD8",
                            borderRadius: 3,
                            overflow: "hidden",
                            boxShadow: "0 4px 20px rgba(0,0,0,0.08)",
                            transition: "all 0.3s ease",
                            "&:hover": {
                                boxShadow: "0 8px 30px rgba(0,0,0,0.12)",
                                transform: "translateY(-2px)"
                            }
                        }}
                    >
                         Header with User Info
                        <CardContent sx={{ pb: 1 }}>
                            <Box display="flex" alignItems="center" justifyContent="space-between" mb={2}>
                                <Box display="flex" alignItems="center" gap={1.5}>
                                    <Avatar
                                        src={reviewData.user.avatar}
                                        alt={reviewData.user.name}
                                        sx={{
                                            width: 48,
                                            height: 48,
                                            border: "3px solid #fff",
                                            boxShadow: "0 2px 8px rgba(0,0,0,0.1)"
                                        }}
                                    />
                                    <Box>
                                        <Box display="flex" alignItems="center" gap={0.5}>
                                            <Typography variant="subtitle1" fontWeight="600" color="#2c3e50">
                                                {reviewData.user.name}
                                            </Typography>
                                            {reviewData.user.isVerified && (
                                                <VerifiedIcon sx={{ fontSize: 16, color: "#4CAF50" }} />
                                            )}
                                        </Box>
                                        <Typography variant="caption" color="text.secondary">
                                            {reviewData.user.location} • {reviewData.date}
                                        </Typography>
                                    </Box>
                                </Box>
                                <IconButton size="small" sx={{ color: "text.secondary" }}>
                                    <MoreVertIcon />
                                </IconButton>
                            </Box>

                             Rating and Project Type
                            <Box display="flex" alignItems="center" justifyContent="space-between" mb={2}>
                                <Box display="flex" alignItems="center" gap={1}>
                                    <Rating
                                        value={reviewData.rating}
                                        readOnly
                                        size="small"
                                        precision={0.5}
                                        sx={{ color: "#FF6B35" }}
                                    />
                                    <Typography variant="body2" fontWeight="500" color="#2c3e50">
                                        {reviewData.rating}
                                    </Typography>
                                </Box>
                                <Chip
                                    label={reviewData.projectType}
                                    size="small"
                                    sx={{
                                        backgroundColor: "#E8F5E8",
                                        color: "#2E7D32",
                                        fontWeight: "500",
                                        fontSize: "0.75rem"
                                    }}
                                />
                            </Box>
                        </CardContent>

                         Images Section
                        <Box sx={{
                            px: 2,
                            pb: 2
                        }}>
                            <Box sx={{
                                display: 'flex',
                                overflowX: 'auto',
                                gap: 1.5,
                                pb: 1,
                                '&::-webkit-scrollbar': {
                                    height: 6,
                                },
                                '&::-webkit-scrollbar-track': {
                                    backgroundColor: 'rgba(0,0,0,0.1)',
                                    borderRadius: 10,
                                },
                                '&::-webkit-scrollbar-thumb': {
                                    backgroundColor: 'rgba(0,0,0,0.3)',
                                    borderRadius: 10,
                                }
                            }}>
                                {reviewData.images.map((imageUrl, index) => (
                                    <Box
                                        key={index}
                                        sx={{
                                            position: 'relative',
                                            minWidth: 180,
                                            height: 120,
                                            borderRadius: 2,
                                            overflow: 'hidden',
                                            cursor: 'pointer',
                                            transition: 'transform 0.2s ease',
                                            '&:hover': {
                                                transform: 'scale(1.02)'
                                            }
                                        }}
                                    >
                                        <CardMedia
                                            component="img"
                                            image={imageUrl}
                                            alt={`Review image ${index + 1}`}
                                            sx={{
                                                width: '100%',
                                                height: '100%',
                                                objectFit: 'cover',
                                            }}
                                        />
                                        <Box
                                            sx={{
                                                position: 'absolute',
                                                top: 8,
                                                right: 8,
                                                backgroundColor: 'rgba(0,0,0,0.6)',
                                                color: 'white',
                                                borderRadius: '50%',
                                                width: 24,
                                                height: 24,
                                                display: 'flex',
                                                alignItems: 'center',
                                                justifyContent: 'center',
                                                fontSize: '0.75rem',
                                                fontWeight: 'bold'
                                            }}
                                        >
                                            {index + 1}
                                        </Box>
                                    </Box>
                                ))}
                            </Box>
                        </Box>

                         Content Section
                        <CardContent sx={{ pt: 0 }}>
                            <Typography
                                variant="body1"
                                sx={{
                                    lineHeight: 1.6,
                                    color: "#2c3e50",
                                    mb: 2
                                }}
                            >
                                {reviewData.content}
                            </Typography>

                            <Divider sx={{ mb: 2, backgroundColor: "rgba(0,0,0,0.08)" }} />

                             Actions
                            <Box display="flex" alignItems="center" justifyContent="between" gap={2}>
                                <Box display="flex" alignItems="center" gap={1}>
                                    <IconButton
                                        size="small"
                                        sx={{
                                            color: reviewData.helpful ? "#4CAF50" : "text.secondary",
                                            "&:hover": { backgroundColor: "rgba(76, 175, 80, 0.1)" }
                                        }}
                                    >
                                        <ThumbUpIcon fontSize="small" />
                                    </IconButton>
                                    <Typography variant="body2" color="text.secondary">
                                        {reviewData.likes} helpful
                                    </Typography>
                                </Box>

                                <Box display="flex" alignItems="center" gap={1}>
                                    <IconButton
                                        size="small"
                                        sx={{
                                            color: "text.secondary",
                                            "&:hover": { backgroundColor: "rgba(0,0,0,0.05)" }
                                        }}
                                    >
                                        <ReplyIcon fontSize="small" />
                                    </IconButton>
                                    <Typography variant="body2" color="text.secondary">
                                        Reply
                                    </Typography>
                                </Box>
                            </Box>
                        </CardContent>
                    </Card>*/}
            <Container maxWidth="lg" sx={{ py: 6 }}>
                <Typography variant="h4" component="h2" gutterBottom sx={{ fontWeight: 'bold', mb: 4 }}>
                    Reviews <ReviewsIcon></ReviewsIcon>
                </Typography>

                {/* Write a Review Section */}
                {/* FAB */}
                <Tooltip title="Write your review" arrow>
                    <Fab
                        color="secondary"
                        aria-label="edit"
                        onClick={handleOpen}
                        sx={{
                            position: 'fixed',
                            bottom: 16,
                            right: 16,
                        }}
                    >
                        <EditIcon />
                    </Fab>
                </Tooltip>

                {/* Dialog */}
                <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
                    <DialogTitle>
                        Write a Review
                        <IconButton
                            onClick={handleClose}
                            sx={{ position: 'absolute', right: 8, top: 8 }}
                        >
                            <CloseIcon />
                        </IconButton>
                    </DialogTitle>

                    <DialogContent dividers>
                        {/*<TextField
                            label="Your Name"
                            fullWidth
                            margin="dense"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                        />*/}

                        <Box display="flex" alignItems="center" gap={2} mt={2} mb={2}>
                            <Typography component="legend">Rating</Typography>
                            <Rating
                                name="rating"
                                value={rating}
                                onChange={(e, newValue) => setRating(newValue)}
                            />
                        </Box>

                        <TextField
                            label="Your Review"
                            multiline
                            rows={4}
                            fullWidth
                            margin="dense"
                            value={review}
                            onChange={(e) => setReview(e.target.value)}
                        />

                        <Box mt={3}>
                            <Typography variant="body2" gutterBottom>
                                Upload Images (Multiple):
                            </Typography>
                            <Button
                                variant="outlined"
                                component="label"
                                startIcon={<PhotoCameraIcon />}
                            >
                                Upload
                                <input
                                    type="file"
                                    hidden
                                    multiple
                                    accept="image/*"
                                    onChange={handleImageChange}
                                />
                            </Button>

                            {/* Show preview of uploaded files */}
                            {images.length > 0 && (
                                <Box mt={2} display="flex" gap={1} flexWrap="wrap">
                                    {images.map((img, idx) => (
                                        <img
                                            key={idx}
                                            src={URL.createObjectURL(img)}
                                            alt={`preview-${idx}`}
                                            width={80}
                                            height={60}
                                            style={{ objectFit: 'cover', borderRadius: 4 }}
                                        />
                                    ))}
                                </Box>
                            )}
                        </Box>
                    </DialogContent>

                    <DialogActions>
                        <Button onClick={handleClose} color="inherit">Cancel</Button>
                        <Button onClick={handleSubmit} variant="contained" color="secondary">
                            Submit
                        </Button>
                    </DialogActions>
                </Dialog>




                    <Stack direction="row"
                           spacing={8}
                           sx={{
                               justifyContent: "flex-start",
                               alignItems: "center",
                           }}>
                        {paginatedReviews.map((reviewData) => (
                            <Item key={reviewData.id} disableGutters sx={{ mb: 4,}}>
                                {/* Your Full Custom Card Component Here */}
                                <Card
                                    sx={{
                                        width: "100%",
                                        maxWidth: 600,
                                        margin: "0 auto",
                                        backgroundColor: "#FFEAD8",
                                        borderRadius: 3,
                                        overflow: "hidden",
                                        boxShadow: "0 4px 20px rgba(0,0,0,0.08)",
                                        transition: "all 0.3s ease",
                                        "&:hover": {
                                            boxShadow: "0 8px 30px rgba(0,0,0,0.12)",
                                            transform: "translateY(-2px)",
                                        },
                                    }}
                                >
                                    {/* Header */}
                                    <CardContent sx={{ pb: 1 }}>
                                        <Box display="flex" alignItems="center" justifyContent="space-between" mb={2}>
                                            <Box display="flex" alignItems="center" gap={1.5}>
                                                <Avatar
                                                    src={reviewData.user.avatar}
                                                    alt={reviewData.user.name}
                                                    sx={{
                                                        width: 48,
                                                        height: 48,
                                                        border: "3px solid #fff",
                                                        boxShadow: "0 2px 8px rgba(0,0,0,0.1)",
                                                    }}
                                                />
                                                <Box>
                                                    <Box display="flex" alignItems="center" gap={0.5}>
                                                        <Typography variant="subtitle1" fontWeight="600" color="#2c3e50">
                                                            {reviewData.user.name}
                                                        </Typography>
                                                        {reviewData.user.isVerified && (
                                                            <VerifiedIcon sx={{ fontSize: 16, color: "#4CAF50" }} />
                                                        )}
                                                    </Box>
                                                    <Typography variant="caption" color="text.secondary">
                                                        {reviewData.user.location} • {reviewData.date}
                                                    </Typography>
                                                </Box>
                                            </Box>
                                            <IconButton size="small" sx={{ color: "text.secondary" }}>
                                                <MoreVertIcon />
                                            </IconButton>
                                        </Box>

                                        <Box display="flex" alignItems="center" justifyContent="space-between" mb={2}>
                                            <Box display="flex" alignItems="center" gap={1}>
                                                <Rating
                                                    value={reviewData.rating}
                                                    readOnly
                                                    size="small"
                                                    precision={0.5}
                                                    sx={{ color: "#FF6B35" }}
                                                />
                                                <Typography variant="body2" fontWeight="500" color="#2c3e50">
                                                    {reviewData.rating}
                                                </Typography>
                                            </Box>
                                            <Chip
                                                label={reviewData.projectType}
                                                size="small"
                                                sx={{
                                                    backgroundColor: "#E8F5E8",
                                                    color: "#2E7D32",
                                                    fontWeight: "500",
                                                    fontSize: "0.75rem",
                                                }}
                                            />
                                        </Box>
                                    </CardContent>

                                    {/* Images */}
                                    <Box sx={{ px: 2, pb: 2 }}>
                                        <Box
                                            sx={{
                                                display: "flex",
                                                overflowX: "auto",
                                                gap: 1.5,
                                                pb: 1,
                                                "&::-webkit-scrollbar": {
                                                    height: 6,
                                                },
                                                "&::-webkit-scrollbar-track": {
                                                    backgroundColor: "rgba(0,0,0,0.1)",
                                                    borderRadius: 10,
                                                },
                                                "&::-webkit-scrollbar-thumb": {
                                                    backgroundColor: "rgba(0,0,0,0.3)",
                                                    borderRadius: 10,
                                                },
                                            }}
                                        >
                                            {reviewData.images.map((imageUrl, index) => (
                                                <Box
                                                    key={index}
                                                    sx={{
                                                        position: "relative",
                                                        minWidth: 180,
                                                        height: 120,
                                                        borderRadius: 2,
                                                        overflow: "hidden",
                                                        cursor: "pointer",
                                                        transition: "transform 0.2s ease",
                                                        "&:hover": {
                                                            transform: "scale(1.02)",
                                                        },
                                                    }}
                                                >
                                                    <CardMedia
                                                        component="img"
                                                        image={imageUrl}
                                                        alt={`Review image ${index + 1}`}
                                                        sx={{
                                                            width: "100%",
                                                            height: "100%",
                                                            objectFit: "cover",
                                                        }}
                                                    />
                                                    <Box
                                                        sx={{
                                                            position: "absolute",
                                                            top: 8,
                                                            right: 8,
                                                            backgroundColor: "rgba(0,0,0,0.6)",
                                                            color: "white",
                                                            borderRadius: "50%",
                                                            width: 24,
                                                            height: 24,
                                                            display: "flex",
                                                            alignItems: "center",
                                                            justifyContent: "center",
                                                            fontSize: "0.75rem",
                                                            fontWeight: "bold",
                                                        }}
                                                    >
                                                        {index + 1}
                                                    </Box>
                                                </Box>
                                            ))}
                                        </Box>
                                    </Box>

                                    {/* Content */}
                                    <CardContent sx={{ pt: 0 }}>
                                        <Typography
                                            variant="body1"
                                            sx={{
                                                lineHeight: 1.6,
                                                color: "#2c3e50",
                                                mb: 2,
                                            }}
                                        >
                                            {reviewData.content}
                                        </Typography>

                                        <Divider sx={{ mb: 2, backgroundColor: "rgba(0,0,0,0.08)" }} />

                                        {/* Actions */}
                                        <Box display="flex" alignItems="center" justifyContent="space-between" gap={2}>
                                            <Box display="flex" alignItems="center" gap={1}>
                                                <IconButton
                                                    size="small"
                                                    sx={{
                                                        color: reviewData.helpful ? "#4CAF50" : "text.secondary",
                                                        "&:hover": { backgroundColor: "rgba(76, 175, 80, 0.1)" },
                                                    }}
                                                >
                                                    <ThumbUpIcon fontSize="small" />
                                                </IconButton>
                                                <Typography variant="body2" color="text.secondary">
                                                    {reviewData.likes} helpful
                                                </Typography>
                                            </Box>
                                        </Box>
                                    </CardContent>
                                </Card>
                            </Item>
                        ))}
                    </Stack>
                <Box display="flex" justifyContent="center">
                    <Pagination
                        count={Math.ceil(reviewDataList.length / reviewsPerPage)}
                        page={page}
                        onChange={handleChangePage}
                        color="primary"
                    />
                </Box>

            </Container>


        </Box>
    )

}






export default DesignerDetail;



