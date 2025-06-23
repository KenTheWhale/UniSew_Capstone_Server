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
    Typography
} from "@mui/material";
import MyLocationIcon from '@mui/icons-material/MyLocation';
import WorkIcon from '@mui/icons-material/Work';
import Container from '@mui/material/Container';
import FavoriteIcon from '@mui/icons-material/Favorite';
import ShareIcon from '@mui/icons-material/Share';
import MailIcon from '@mui/icons-material/Mail';
import PhoneIcon from '@mui/icons-material/Phone';
import ReviewsIcon from '@mui/icons-material/Reviews';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { DateCalendar } from '@mui/x-date-pickers/DateCalendar';

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
    const reviewdata = [
        {
            id: 1,
            ratings: 5,
            content: 'Excellent logo design service! Quick turnaround and exactly what I wanted.',
            createdDate: '2025-06-22T10:30:00Z',
            images: ['/img1.jpg', '/img2.jpg'],
        },
        {
            id: 2,
            ratings: 4,
            content: 'Excellent logo design service! Quick turnaround and exactly what I wanted.',
            createdDate: '2025-06-21T14:20:00Z',
            images: ['/img3.jpg'],
        },
        {
            id: 3,
            ratings: 5,
            content: 'Excellent logo design service! Quick turnaround and exactly what I wanted.',
            createdDate: '2025-06-19T08:45:00Z',
            images: ['/img4.jpg', '/img5.jpg', '/img6.jpg'],
        },
    ];



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
                        View My Services
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
            <Container maxWidth="lg" sx={{ py: 6 }}>
                <Typography variant="h4" component="h2" gutterBottom sx={{ fontWeight: 'bold', mb: 4 }}>
                    Reviews <ReviewsIcon></ReviewsIcon>
                </Typography>
                <Stack
                    direction="column"
                    spacing={2}
                    sx={{
                        justifyContent: "center",
                        alignItems: "stretch",
                    }}
                >

                    <List>
                        {reviewdata.map((item)  => (
                            <ListItem key={item.id}>
                                <Card sx={{
                                    width: "100%",
                                    display: "flex",
                                    flexDirection: "column",
                                    justifyContent: "center",
                                    backgroundColor: "#FFEAD8"
                                }}>

                                    {/* Images Section */}
                                    <Box sx={{ display: 'flex', overflowX: 'auto' }}>
                                        {item.images.map((imageUrl, index) => (
                                            <CardMedia
                                                key={index}
                                                component="img"
                                                image={imageUrl}
                                                alt={`Review image ${index + 1}`}
                                                sx={{
                                                    height: 150,
                                                    width: 200,
                                                    objectFit: 'cover',
                                                    mr: 1,
                                                    borderRadius: 1,
                                                }}
                                            />
                                        ))}
                                    </Box>


                                    {/* Content Section */}
                                    <CardContent>
                                        <Box display="flex" alignItems="center" justifyContent="space-between">
                                            <Rating value={item.ratings} readOnly size="small" />
                                            <Typography variant="caption" color="text.secondary">
                                                None
                                            </Typography>
                                        </Box>

                                        <Typography variant="body1" sx={{ mt: 1 }}>
                                            {item.content}
                                        </Typography>
                                    </CardContent>

                                </Card>
                            </ListItem>
                        ))}
                    </List>
                </Stack>
            </Container>
        </Box>
    )

}
export default DesignerDetail;



