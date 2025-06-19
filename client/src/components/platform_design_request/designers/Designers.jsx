import { useState } from 'react';
import {
    Box,
    Drawer,
    TextField,
    Typography,
    Card,
    CardMedia,
    CardContent,
    CardActions,
    Button,
    Grid,
    Pagination,
    Select,
    MenuItem,
    InputLabel,
    FormControl
} from '@mui/material';
import Image from '../../../assets/s-l1600.png';


const designerList = [
    { name: 'Designer 1', description: 'Top 1 Logo Designer', category: 'Logo' },
    { name: 'Designer 2', description: 'Award-Winning Illustrator', category: 'Illustration' },
    { name: 'Designer 3', description: 'Creative UI/UX Expert', category: 'UI/UX' },
    { name: 'Designer 4', description: 'Branding Specialist', category: 'Logo' },
    { name: 'Designer 5', description: 'Freelance Visual Artist', category: 'Illustration' },
    { name: 'Designer 6', description: 'Typography Master', category: 'Illustration' },
    { name: 'Designer 7', description: 'Logo Expert', category: 'Logo' },
    { name: 'Designer 8', description: 'Motion Designer', category: 'Illustration' },
    { name: 'Designer 9', description: 'UX Researcher', category: 'UI/UX' },
    { name: 'Designer 10', description: 'App UI Designer', category: 'UI/UX' },
    { name: 'Designer 11', description: 'Concept Artist', category: 'Illustration' },
    { name: 'Designer 12', description: 'Freelance Designer', category: 'Logo' }
];

export default function Designers() {
    const [filter, setFilter] = useState('');
    const [category, setCategory] = useState('');
    const [page, setPage] = useState(1);
    const cardsPerPage = 9;

    const handleReset = () => {
        setFilter('');
        setCategory('');
        setPage(1);
    };

    const filteredDesigners = designerList.filter((designer) => {
        const matchesText = designer.name.toLowerCase().includes(filter.toLowerCase()) ||
            designer.description.toLowerCase().includes(filter.toLowerCase());
        const matchesCategory = category === '' || designer.category === category;
        return matchesText && matchesCategory;
    });

    const pageCount = Math.ceil(filteredDesigners.length / cardsPerPage);
    const paginatedDesigners = filteredDesigners.slice(
        (page - 1) * cardsPerPage,
        page * cardsPerPage
    );

    const handlePageChange = (event, value) => {
        setPage(value);
    };

    return (
        <Box sx={{ display: 'flex', minHeight: '100vh', paddingTop: '64px', paddingBottom: '80px' }}>
            {/* Fixed Drawer */}
            <Drawer
                variant="permanent"
                anchor="left"
                sx={{
                    width: 240,
                    flexShrink: 0,
                    '& .MuiDrawer-paper': {
                        width: 240,
                        boxSizing: 'border-box',
                        padding: 2,
                        paddingTop: '64px',
                        marginTop: 5
                    }
                }}
            >
                <Typography variant="h6" sx={{ mb: 2 }}>
                    Filter Designers
                </Typography>

                {/* Search */}
                <TextField
                    label="Search by name or role"
                    variant="outlined"
                    fullWidth
                    value={filter}
                    onChange={(e) => {
                        setFilter(e.target.value);
                        setPage(1);
                    }}
                />


                {/* Category Filter */}
                <FormControl fullWidth sx={{ mt: 2 }}>
                    <InputLabel>Category</InputLabel>
                    <Select
                        value={category}
                        label="Category"
                        onChange={(e) => {
                            setCategory(e.target.value);
                            setPage(1);
                        }}
                    >
                        <MenuItem value="">All</MenuItem>
                        <MenuItem value="Logo">Logo</MenuItem>
                        <MenuItem value="UI/UX">UI/UX</MenuItem>
                        <MenuItem value="Illustration">Illustration</MenuItem>
                    </Select>
                </FormControl>

                <Button variant="outlined" sx={{ mt: 2 }} fullWidth onClick={handleReset}>
                    Reset
                </Button>
            </Drawer>

            {/* Main Content */}
            <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
                <Grid container spacing={3}>
                    {paginatedDesigners.map((designer, index) => (
                        <Grid item xs={12} sm={6} md={4} key={index}>
                            <Card sx={{ maxWidth: 300 }}>
                                <CardMedia
                                    component="img"
                                    image={Image}
                                    alt={designer.name}
                                    sx={{
                                        height: 200,
                                        width: '100%',
                                        objectFit: 'cover',
                                        borderBottom: '1px solid #ddd',
                                        borderRadius: '4px 4px 0 0'
                                    }}
                                />
                                <CardContent>
                                    <Typography gutterBottom variant="h5">
                                        {designer.name}
                                    </Typography>
                                    <Typography variant="body2" sx={{ color: 'text.secondary' }}>
                                        {designer.description}
                                    </Typography>
                                </CardContent>
                                <CardActions>
                                    <Button size="small">Share</Button>
                                    <Button size="small">Learn More</Button>
                                </CardActions>
                            </Card>
                        </Grid>
                    ))}
                </Grid>

                {/* Pagination */}
                <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
                    <Pagination
                        count={pageCount}
                        page={page}
                        onChange={handlePageChange}
                        color="primary"
                    />
                </Box>
            </Box>
        </Box>
    );
}