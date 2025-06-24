import { LineChart } from '@mui/x-charts/LineChart';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';

const salesData = [
    { label: 'W1', value: 400 },
    { label: 'W2', value: 600 },
    { label: 'W3', value: 900 },
    { label: 'W4', value: 750 },
];

const DashboardChart = () => {
    return (
        <div className="dashboardChartBox">
            <div className="d-flex justify-content-between align-items-start">
                <div>
                    <h4>Total Sales</h4>
                    <span className="fs-4 fw-bold">$8,430</span>
                </div>
                <div className="icon-container">
                    <AttachMoneyIcon className="text-white fs-5" />
                </div>
            </div>

            <div className="chart-area">
                <LineChart
                    xAxis={[{ scaleType: 'point', data: salesData.map(d => d.label) }]}
                    series={[{ data: salesData.map(d => d.value), color: '#ffffff' }]}
                    height={80}
                    margin={{ top: 10, bottom: 10, left: 10, right: 10 }}
                    grid={{ horizontal: false, vertical: false }}
                    sx={{
                        '.MuiLineElement-root': {
                            strokeWidth: 2,
                            stroke: '#fff',
                        },
                        '.MuiMarkElement-root': {
                            display: 'none',
                        },
                        '.MuiChartsAxis-root': {
                            display: 'none',
                        },
                    }}
                />
            </div>

            <h6 className="mb-0">Last Month</h6>
        </div>
    );
};
export default DashboardChart;