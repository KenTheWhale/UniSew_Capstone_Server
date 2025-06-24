import '../../styles/auth/Home.css';
import { Container, Row, Col, Button, Carousel, Card } from 'react-bootstrap';
import heroBg from '../../assets/sewing.jpg'
import {enqueueSnackbar} from "notistack";

function HeroSection() {
    return (
        <section className="uni-hero d-flex align-items-center" style={{ backgroundImage: `url(${heroBg})` }}>
            <Container>
                <Row>
                    <Col md={7} className="hero-content">
                        <h1 className="display-4 fw-bold text-white mb-4">Find the right service, right away</h1>
                        <form className="d-flex mb-3">
                            <input className="form-control rounded-start-pill" placeholder="Search for any service" />
                            <Button variant="primary" className="rounded-end-pill px-4 fw-bold">Search</Button>
                        </form>
                        <div className="text-white small">
                            Popular:&nbsp;
                            <span className="hero-popular-link">Uniform Design</span>,&nbsp;
                            <span className="hero-popular-link">Logo Design</span>
                        </div>
                    </Col>
                </Row>
            </Container>
        </section>
    );
}

function TrustedBySection() {
    return (
        <section className="trusted-by py-3 bg-light">
            <Container>
                <div className="d-flex align-items-center justify-content-center gap-4">
                    <span className="trusted-label">Trusted by:</span>
                    <img src="/google.png" alt="Paypal" height={25} />
                    <img src="/payos.png" alt="Google" height={35} />
                </div>
            </Container>
        </section>
    );
}

function PopularServicesSection() {
    const services = [
        { img: '/designer.jpg', label: 'Adam', desc: 'Uni Pro' },
        { img: '/designer.jpg', label: 'Eva', desc: 'Uni Pro' },
        { img: '/designer.jpg', label: 'Saber', desc: 'Uni Pro' },
        { img: '/designer.jpg', label: 'Cartethyia', desc: 'Uni Pro' },
        { img: '/designer.jpg', label: 'Chisa', desc: 'Uni Pro' },
    ];
    return (
        <section className="popular-services py-5">
            <Container>
                <h2 className="section-title mb-4">Popular designer</h2>
                <Carousel indicators={false} interval={3500}>
                    {services.map((sv, idx) => (
                        <Carousel.Item key={idx}>
                            <div className="d-flex justify-content-center">
                                <Card style={{ width: '16rem' }} className="shadow-sm mx-2">
                                    <Card.Img variant="top" src={sv.img} />
                                    <Card.Body>
                                        <Card.Title>{sv.label}</Card.Title>
                                        <Card.Text className="text-muted">{sv.desc}</Card.Text>
                                    </Card.Body>
                                </Card>
                            </div>
                        </Carousel.Item>
                    ))}
                </Carousel>
            </Container>
        </section>
    );
}

function HowItWorksSection() {
    return (
        <section className="how-it-works py-5 bg-light">
            <Container>
                <h2 className="section-title mb-4 text-center">A whole world of uniform designer at your fingertips</h2>
                <Row className="text-center">
                    <Col md={3}>
                        <img src="/icon-checked.svg" alt="" className="mb-2" height={48} />
                        <h5>Proof of quality</h5>
                        <p>Check any pro’s work samples, client reviews, and more before you hire.</p>
                    </Col>
                    <Col md={3}>
                        <img src="/icon-wallet.svg" alt="" className="mb-2" height={48} />
                        <h5>No cost until you hire</h5>
                        <p>Interview potential fits for your job, negotiate rates, and only pay for work you approve.</p>
                    </Col>
                    <Col md={3}>
                        <img src="/icon-fast.svg" alt="" className="mb-2" height={48} />
                        <h5>Quick turnaround</h5>
                        <p>Receive quality work quickly thanks to our global network of freelancers.</p>
                    </Col>
                    <Col md={3}>
                        <img src="/icon-support.svg" alt="" className="mb-2" height={48} />
                        <h5>24/7 support</h5>
                        <p>Questions? Our round-the-clock support team is available to help anytime.</p>
                    </Col>
                </Row>
            </Container>
        </section>
    );
}

function FeaturedCategoriesSection() {
    const categories = [
        { img: '/JG.png', title: 'Jiayung' },
        { img: '/stussy.png', title: 'Stussy' },
        { img: '/broklyn.jpg', title: 'Broklyn' },
        { img: '/blossom.png', title: 'Blossom' },
        { img: '/cosy.jpg', title: 'Cosy' },
        { img: '/hollister.png', title: 'Hollister' },
        { img: '/lismiknits.jpg', title: 'Lismiknits' },
        { img: '/Valentino.png', title: 'Valentino' },
    ];
    return (
        <section className="featured-categories py-5">
            <Container>
                <h2 className="section-title mb-4">Explore UniSew Garment Factory</h2>
                <Row>
                    {categories.map((cat, idx) => (
                        <Col md={3} sm={6} xs={12} className="mb-4" key={idx}>
                            <Card className="category-card text-center h-100 shadow-sm">
                                <Card.Img variant="top" src={cat.img} height={120} style={{ objectFit: 'cover' }} />
                                <Card.Body>
                                    <Card.Title>{cat.title}</Card.Title>
                                </Card.Body>
                            </Card>
                        </Col>
                    ))}
                </Row>
            </Container>
        </section>
    );
}

function BusinessSolutionsSection() {
    return (
        <section className="business-solutions py-5 bg-success text-white text-center">
            <Container>
                <Row className="align-items-center">
                    <Col md={8}>
                        <h3 className="fw-bold mb-3">UniSew Business Solutions</h3>
                        <p>Advanced solutions and services for school uniform</p>
                    </Col>
                    <Col md={4}>
                        <Button variant="light" size="lg" className="fw-bold">Learn More</Button>
                    </Col>
                </Row>
            </Container>
        </section>
    );
}

function TestimonialsSection() {
    const testimonials = [
        {
            avatar: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSu93al_iOZAIP-60vS24JSfHTXitp9sHTZSA&s',
            name: 'Tiểu Học Võ Văn Hát',
            text: 'UniSew helped me find exactly the design I wanted in record time!',
            job: 'School'
        },
        {
            avatar: 'https://ueed.edu.vn/wp-content/uploads/2022/11/tieu-hoc-linh-dong.png',
            name: 'Tiểu học Linh Đông',
            text: 'The designer I hired was professional and quick. 10/10 experience.',
            job: 'School'
        },
        {
            avatar: 'https://steamsolutions.com.vn/wp-content/uploads/2024/03/TRUONG-LE-VAN-VIET-768x768.jpg',
            name: 'Tiểu Học Lê Văn Việt',
            text: 'I use UniSew for all my needs. Always satisfied.',
            job: 'School'
        }
    ];
    return (
        <section className="testimonials py-5 bg-light">
            <Container>
                <h2 className="section-title mb-4 text-center">What schools said</h2>
                <Row className="justify-content-center">
                    {testimonials.map((t, idx) => (
                        <Col md={4} sm={6} xs={12} className="mb-4" key={idx}>
                            <Card className="testimonial-card shadow-sm p-3">
                                <div className="d-flex align-items-center mb-3">
                                    <img src={t.avatar} alt={t.name} className="rounded-circle me-3" width={48} height={48} />
                                    <div>
                                        <strong>{t.name}</strong>
                                        <div className="small text-muted">{t.job}</div>
                                    </div>
                                </div>
                                <Card.Text>"{t.text}"</Card.Text>
                            </Card>
                        </Col>
                    ))}
                </Row>
            </Container>
        </section>
    );
}

function CTABottomSection() {
    return (
        <section className="cta-bottom py-5 bg-dark text-white text-center">
            <Container>
                <h2 className="mb-3 fw-bold">Ready to get started?</h2>
                <p className="mb-4">Join thousands of businesses using UniSew to get work done.</p>
                <Button variant="primary" size="lg" className="fw-bold px-5">Join UniSew</Button>
            </Container>
        </section>
    );
}

function UnisewProSection() {
    return (
        <section className="uni-pro-section py-5 bg-white border-top border-bottom">
            <div className="container">
                <div className="row align-items-center flex-column-reverse flex-lg-row">
                    {/* Left content */}
                    <div className="col-lg-6 col-12 mt-4 mt-lg-0">
                        <h2 className="fw-bold mb-3" style={{ color: '#034efc' }}>UniSew Pro</h2>
                        <h5 className="mb-4">The premium solution for businesses</h5>
                        <ul className="list-unstyled mb-4">
                            <li className="mb-3 d-flex">
                                <span className="me-3" style={{ color: '#1dbf73', fontWeight: 700, fontSize: 22 }}>✓</span>
                                <div>
                                    <div className="fw-semibold">Dedicated hiring experts</div>
                                    <small>Count on an account manager to find you the right talent and see to your project’s every need.</small>
                                </div>
                            </li>
                            <li className="mb-3 d-flex">
                                <span className="me-3" style={{ color: '#1dbf73', fontWeight: 700, fontSize: 22 }}>✓</span>
                                <div>
                                    <div className="fw-semibold">Satisfaction guarantee</div>
                                    <small>Order confidently, with guaranteed refunds for less-than-satisfactory deliveries.</small>
                                </div>
                            </li>
                            <li className="mb-3 d-flex">
                                <span className="me-3" style={{ color: '#1dbf73', fontWeight: 700, fontSize: 22 }}>✓</span>
                                <div>
                                    <div className="fw-semibold">Advanced management tools</div>
                                    <small>Seamlessly integrate freelancers into your team and projects.</small>
                                </div>
                            </li>
                            <li className="mb-3 d-flex">
                                <span className="me-3" style={{ color: '#1dbf73', fontWeight: 700, fontSize: 22 }}>✓</span>
                                <div>
                                    <div className="fw-semibold">Flexible payment models</div>
                                    <small>Pay per project or opt for hourly rates to facilitate longer-term collaboration.</small>
                                </div>
                            </li>
                        </ul>
                        <a
                            href="#"
                            className="btn btn-primary btn-lg px-5"
                            style={{ borderRadius: 40, fontWeight: 600 }}
                        >
                            Try now &rarr;
                        </a>
                    </div>
                    {/* Right image */}
                    <div className="col-lg-6 col-12 text-center">
                        <img
                            src="/pro.jpg"
                            alt="Unisew Pro"
                            className="img-fluid rounded"
                            style={{ maxHeight: 330, objectFit: 'cover' }}
                        />
                    </div>
                </div>
            </div>
        </section>
    );
}

function DesignHeroSection() {
    return (
        <section className="design-hero-section d-flex align-items-center" style={{ minHeight: 350, background: '#f4f6fa' }}>
            <div className="container py-5">
                <div className="row align-items-center">
                    <div className="col-md-8">
                        <h1 className="display-5 fw-bold mb-3" style={{ color: '#002f6c' }}>
                            Your business deserves great design
                        </h1>
                        <p className="lead mb-4" style={{ color: '#555', maxWidth: 600 }}>
                            Logos, websites, packaging design and more. Our trusted designer community has helped thousands of businesses launch, grow, expand and rebrand with custom, professional design.
                        </p>
                        <a
                            href="/school"
                            className="btn btn-primary btn-lg px-4"
                            style={{
                                backgroundColor: '#002f6c',
                                borderColor: '#002f6c',
                                borderRadius: 40,
                                fontWeight: 600
                            }}
                        >
                            Start your design
                        </a>
                    </div>
                    {/* Optional: Right side image/graphic */}
                    <div className="col-md-4 d-none d-md-block text-end">
                        <img
                            src="https://preview.redd.it/xepxacsbxds91.jpg?width=640&crop=smart&auto=webp&s=37c815d17ebf2d0e424ed7e0834a77510377e9a8"
                            alt="Design Illustration"
                            className="img-fluid"
                            style={{ maxHeight: 300 }}
                        />
                    </div>
                </div>
            </div>
        </section>
    );
}

function ExpertTrustSection() {
    return (
        <section className="expert-trust-section py-5 bg-white border-top border-bottom">
            <div className="container">
                <div className="row align-items-center">
                    {/* Icon or illustration (optional) */}
                    <div className="col-md-5 d-none d-md-flex justify-content-center">
                        <img
                            src="/expert.jpg"
                            alt="Trusted Experts"
                            style={{ maxHeight: 500, objectFit: 'cover' }}
                            className="img-fluid"
                        />
                    </div>
                    {/* Main content */}
                    <div className="col-md-7">
                        <h2 className="fw-bold mb-2" style={{ color: '#002f6c' }}>
                            Work with creative experts you can trust
                        </h2>
                        <p className="mb-4" style={{ color: '#555', maxWidth: 640 }}>
                            Feel confident working with our designer community. All our designers are vetted creative experts who've worked with hundreds of businesses to bring their designs to life.
                        </p>
                        <a
                            href="/designer/list"
                            className="btn btn-outline-primary btn-lg px-4"
                            style={{
                                borderRadius: 40,
                                fontWeight: 600,
                                color: '#002f6c',
                                borderColor: '#002f6c'
                            }}
                        >
                            Browse designer portfolios
                        </a>
                    </div>
                </div>
            </div>
        </section>
    );
}


function RenderPage() {
    if(localStorage.getItem("message") && localStorage.getItem("variant")){
        enqueueSnackbar(localStorage.getItem("message"), {variant: localStorage.getItem("variant")})
        localStorage.removeItem("message")
        localStorage.removeItem("variant")
    }

    return (
        <>
            <HeroSection />
            <TrustedBySection />
            <DesignHeroSection />
            <ExpertTrustSection />
            <PopularServicesSection />
            <FeaturedCategoriesSection />
            <HowItWorksSection />
            <UnisewProSection />
            <TestimonialsSection />
            <BusinessSolutionsSection />
            <CTABottomSection />
        </>
    );
}

export default function Home() {
    return <RenderPage />;
}
