import {
    Box,
    Button,
    Paper,
    Stack,
    Typography,
    TextField,
    Radio,
    RadioGroup,
    FormControlLabel,
    FormControl, Table, TableHead, TableRow, TableCell, Checkbox, TableBody, Chip, MenuItem,
} from "@mui/material";
import GetAppIcon from '@mui/icons-material/GetApp';
import FilterAltIcon from '@mui/icons-material/FilterAlt';
import CreditCardIcon from '@mui/icons-material/CreditCard';
import {useState} from "react";
import {LocalizationProvider} from '@mui/x-date-pickers/LocalizationProvider';

import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import {AdapterDateFns} from "@mui/x-date-pickers/AdapterDateFns";
const invoiceData = [
    {
        title: "Design Accessibility",
        id: "#012500",
        company: "Edmate",
        amount: "$180",
        date: "06/22/2024",
        status: "Paid",
        plan: "Basic",
        logo: "https://cdn-icons-png.flaticon.com/512/753/753345.png",
    },
    {
        title: "Design System",
        id: "#012500",
        company: "Edmate",
        amount: "$250",
        date: "06/22/2024",
        status: "Unpaid",
        plan: "Professional",
        logo: "https://cdn-icons-png.flaticon.com/512/3191/3191646.png",
    },
    {
        title: "Frontend Develop",
        id: "#012500",
        company: "Edmate",
        amount: "$128",
        date: "06/22/2024",
        status: "Paid",
        plan: "Basic",
        logo: "https://cdn-icons-png.flaticon.com/512/753/753345.png",
    },
    {
        title: "Design Usability",
        id: "#012500",
        company: "Edmate",
        amount: "$132",
        date: "06/22/2024",
        status: "Unpaid",
        plan: "Basic",
        logo: "https://cdn-icons-png.flaticon.com/512/753/753345.png",
    },
    {
        title: "Digital Marketing",
        id: "#012500",
        company: "Edmate",
        amount: "$186",
        date: "06/22/2024",
        status: "Paid",
        plan: "Advance",
        logo: "https://cdn-icons-png.flaticon.com/512/753/753318.png",
    },
];




export default function Billing() {

    const [showFilter, setShowFilter] = useState(false);
    const [invoiceType, setInvoiceType] = useState('');
    const [amount, setAmount] = useState('');
    const [date, setDate] = useState(null);
    const [plan, setPlan] = useState('');

    return (
        <>
            <Paper variant="outlined" sx={{ p: 4,  borderRadius: 3,
                overflow: 'hidden',

                }}>
                <Typography variant="h6" fontWeight={700}>
                    Payment Method
                </Typography>
                <Typography color="text.secondary" sx={{ mb: 3 }}>
                    Update your billing details and address
                </Typography>

                <Box
                    display="flex"
                    flexDirection={{ xs: "column", md: "row" }}
                    gap={3}
                    justifyContent="space-between"
                    alignItems="stretch"
                >
                    {/* Contact Email Section */}
                    <Paper
                        variant="outlined"
                        sx={{
                            flex: 1,
                            borderRadius: 3,
                            overflow: "hidden",
                            display: 'flex',  // Add this
                            flexDirection: 'column'  // Add this
                        }}
                    >
                        <Box p={2} borderBottom="1px solid #eee">
                            <Typography fontWeight={600}>Contact Email</Typography>
                        </Box>

                        <FormControl sx={{ p: 2, flex: 1 }}>  {/* Add flex: 1 here */}
                            <RadioGroup defaultValue="account">
                                <Stack spacing={2} sx={{ height: '100%' }}>  {/* Add height: 100% */}
                                    {/* First Option */}
                                    <Box
                                        display="flex"
                                        justifyContent="space-between"
                                        alignItems="center"
                                        bgcolor="#f1f6fd"
                                        p={2}
                                        borderRadius={2}
                                        sx={{
                                            border: "1px solid #e0e0e0",
                                        }}
                                    >
                                        <Box>
                                            <Typography fontWeight={600}>Send to my email account</Typography>
                                            <Typography variant="body2" color="text.secondary">
                                                exampleinfo@mail.com
                                            </Typography>
                                        </Box>
                                        <Radio value="account" />
                                    </Box>

                                    {/* Second Option */}
                                    <Box
                                        display="flex"
                                        justifyContent="space-between"
                                        alignItems="center"
                                        bgcolor="#f1f6fd"
                                        p={2}
                                        borderRadius={2}
                                        sx={{
                                            border: "1px solid #e0e0e0",
                                            flex: 1  // Add this to make second option take remaining space
                                        }}
                                    >
                                        <Box flex={1} pr={2}>
                                            <Typography fontWeight={600}>Send to an alternative email</Typography>
                                            <TextField
                                                fullWidth
                                                size="small"
                                                placeholder="exampleinfo@gmail.com"
                                                sx={{ mt: 1 }}
                                            />
                                        </Box>
                                        <Radio value="alternative" />
                                    </Box>
                                </Stack>
                            </RadioGroup>
                        </FormControl>
                    </Paper>

                    {/* Card Details Section */}
                    <Paper
                        variant="outlined"
                        sx={{
                            flex: 1,
                            borderRadius: 3,
                            overflow: 'hidden',
                        }}
                    >
                        <Box
                            p={2}
                            borderBottom="1px solid #eee"
                            display="flex"
                            justifyContent="space-between"
                            alignItems="center"
                        >
                            <Typography fontWeight={600}>Card Details</Typography>
                            <Button variant="outlined" size="small">Add New Card</Button>
                        </Box>

                        <Box p={2}>
                            <Stack spacing={2}>
                                {[{
                                    brand: 'Visa',
                                    last4: '5890',
                                    default: true,
                                    logo: 'https://upload.wikimedia.org/wikipedia/commons/4/41/Visa_Logo.png'
                                }, {
                                    brand: 'Mastercard',
                                    last4: '1895',
                                    default: false,
                                    logo: 'https://upload.wikimedia.org/wikipedia/commons/0/04/Mastercard-logo.png'
                                }].map((card, index) => (
                                    <Box
                                        key={index}
                                        display="flex"
                                        alignItems="center"
                                        justifyContent="space-between"
                                        bgcolor="#EEF4FF"
                                        p={2}
                                        borderRadius={2}
                                    >
                                        <Box display="flex" alignItems="center" gap={2}>
                                            <img src={card.logo} alt={card.brand} width={40} />
                                            <Box>
                                                <Typography fontWeight={600}>
                                                    {card.brand} **** **** {card.last4}
                                                </Typography>
                                                <Typography variant="body2" color="text.secondary">
                                                    Up to 60 User and 100GB team data
                                                </Typography>
                                                {card.default && (
                                                    <Typography variant="caption">
                                                        Set as default &nbsp;
                                                        <span style={{ color: "#1976d2", cursor: "pointer" }}>
                            Edit
                          </span>
                                                    </Typography>
                                                )}
                                            </Box>
                                        </Box>
                                        <Radio />
                                    </Box>
                                ))}
                            </Stack>
                        </Box>
                    </Paper>
                </Box>
            </Paper>


            {/* Transaction History */}

            <Paper variant="outlined" sx={{ p: 4,  borderRadius: 3,
                overflow: 'hidden',
                bgcolor: '#f1f6fd',
                marginTop: 5, }}>
                {/* Header */}
                <Box>
                    <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
                        <Box>
                            <Typography variant="h6" fontWeight={700}>Billing History</Typography>
                            <Typography variant="body2" color="text.secondary">
                                See the transaction you made
                            </Typography>
                        </Box>

                        <Stack direction="row" spacing={2}>
                            <Button
                                variant="contained"
                                startIcon={<FilterAltIcon />}
                                onClick={() => setShowFilter(prev => !prev)}
                            >
                                Add Filter
                            </Button>
                            <Button variant="contained" startIcon={<GetAppIcon />}>
                                Download All
                            </Button>
                        </Stack>
                    </Box>
                    {/* Filter Fields */}
                    {showFilter && (
                        <LocalizationProvider dateAdapter={AdapterDateFns}>
                            <Stack
                                direction="row"
                                spacing={2}
                                alignItems="center"
                                justifyContent="flex-start"
                                sx={{ mt: 2, px: 2 }}
                            >
                                <TextField
                                    select
                                    label="Invoice Type"
                                    value={invoiceType}
                                    onChange={(e) => setInvoiceType(e.target.value)}
                                    size="small"
                                    sx={{ minWidth: 160 }}
                                >
                                    <MenuItem value="commercial">Commercial Invoice</MenuItem>
                                    <MenuItem value="proforma">Proforma Invoice</MenuItem>
                                </TextField>

                                <TextField
                                    select
                                    label="Amount"
                                    value={amount}
                                    onChange={(e) => setAmount(e.target.value)}
                                    size="small"
                                    sx={{ minWidth: 100 }}
                                >
                                    <MenuItem value="1">$1</MenuItem>
                                    <MenuItem value="100">$100</MenuItem>
                                </TextField>

                                <DatePicker
                                    label="Date"
                                    value={date}
                                    onChange={(newValue) => setDate(newValue)}
                                    slotProps={{ textField: { size: "small", sx: { minWidth: 140 } } }}
                                />

                                <TextField
                                    label="Plan"
                                    value={plan}
                                    onChange={(e) => setPlan(e.target.value)}
                                    size="small"
                                    sx={{ minWidth: 120 }}
                                />

                                <Button
                                    variant="contained"
                                    color="primary"
                                    sx={{
                                        borderRadius: "30px",
                                        textTransform: "none",
                                        px: 4,
                                        py: 1.2,
                                    }}
                                >
                                    Apply Filter
                                </Button>
                            </Stack>
                        </LocalizationProvider>
                    )}



                </Box>


                {/* Table */}
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell padding="checkbox"><Checkbox /></TableCell>
                            <TableCell><Typography fontWeight={600}>Invoices</Typography></TableCell>
                            <TableCell><Typography fontWeight={600}>Amount</Typography></TableCell>
                            <TableCell><Typography fontWeight={600}>Dates</Typography></TableCell>
                            <TableCell><Typography fontWeight={600}>Status</Typography></TableCell>
                            <TableCell><Typography fontWeight={600}>Plan</Typography></TableCell>
                            <TableCell><Typography fontWeight={600}>Action</Typography></TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {invoiceData.map((item, index) => (
                            <TableRow key={index}>
                                <TableCell padding="checkbox">
                                    <Checkbox />
                                </TableCell>
                                <TableCell>
                                    <Box display="flex" alignItems="center" gap={2}>
                                        <img src={item.logo} alt={item.title} width={36} height={36} style={{ borderRadius: "50%" }} />
                                        <Box>
                                            <Typography fontWeight={600}>{item.title}</Typography>
                                            <Typography variant="body2" color="text.secondary">
                                                {item.company} - {item.id}
                                            </Typography>
                                        </Box>
                                    </Box>
                                </TableCell>
                                <TableCell>{item.amount}</TableCell>
                                <TableCell>{item.date}</TableCell>
                                <TableCell>
                                    <Chip
                                        label={item.status}
                                        color={item.status === "Paid" ? "success" : "info"}
                                        variant="soft"
                                        sx={{ fontWeight: 500 }}
                                    />
                                </TableCell>
                                <TableCell>{item.plan}</TableCell>
                                <TableCell>
                                    <Button variant="soft" size="small" sx={{ borderRadius: 999 }}>Download</Button>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>

                {/* Footer Buttons */}
                <Box display="flex" justifyContent="flex-end" mt={4} gap={2}>
                    <Button variant="outlined" sx={{ borderRadius: 999 }}>Cancel</Button>
                    <Button variant="contained" sx={{ borderRadius: 999 }}>Save Changes</Button>
                </Box>
            </Paper>




        </>





    );
}