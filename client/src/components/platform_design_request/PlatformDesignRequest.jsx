import {
    Box,
    Typography,
    Button,
    TextField,
    Grid,
    Rating,
} from '@mui/material';

import { useState } from 'react';
import '../../styles/design_request/designRequest.css';
/*import sampleImage  from '../../assets/s-l1600.png'*/


function DesignRequestForm() {
    const [quantity, setQuantity] = useState(1);
    const [uploadedImages, setUploadedImages] = useState([]);

    const handleQuantityChange = (e) => {
        const value = Math.max(1, parseInt(e.target.value) || 1);
        setQuantity(value);
    };

    const handleAddToCart = () => {
        alert(`Added ${quantity} item(s) to cart!`);
    };

    const handleImageUpload = (e) => {
        const files = Array.from(e.target.files);
        const imageURLs = files.map((file) => URL.createObjectURL(file));
        setUploadedImages((prevImages) => [...prevImages, ...imageURLs]);
    };

    const product = {
        title: "Premium School Uniform Set",
        description: "High-quality and comfortable uniform set for primary school students, designed to last and look professional.",
        price: 350000,
        rating: 4.3,
    };


    return (


        <div className="container mt-5 " >

            <div className="row row-container align-items-start">
                {/* Images on the Left */}
                <div className="col-md-8">
                    <div className="row">
                        {uploadedImages.length > 0 ? (
                            uploadedImages.map((img, index) => (
                                <div key={index} className="col-md-6 mb-3">
                                    <img src={img} alt={`Uploaded ${index}`} className="img-fluid rounded" />
                                </div>
                            ))
                        ) : (
                            <div
                                style={{
                                    width: '100%',
                                    height: 300,
                                    backgroundColor: '#f0f0f0',
                                    borderRadius: '8px',
                                }}
                            />
                        )}
                    </div>
                    <input
                        type="file"
                        accept="image/*"
                        multiple
                        onChange={handleImageUpload}
                        className="form-control mt-3"
                    />
                </div>

                {/* Info on the Right */}
                <div className="col-md-4">
                    <h3>{product.title}</h3>
                    <p className="text-primary fs-5">{product.price.toLocaleString()}₫</p>
                    <p>{product.description}</p>

                    <div className="mb-3">
                        <label htmlFor="quantity" className="form-label">Quantity</label>
                        <input
                            type="number"
                            id="quantity"
                            min="1"
                            value={quantity}
                            onChange={handleQuantityChange}
                            className="form-control"
                            style={{ maxWidth: '100px' }}
                        />
                    </div>

                    <button className="btn btn-primary" onClick={handleAddToCart}>
                        Add to Cart
                    </button>
                </div>
            </div>
        </div>

    );
}

export default DesignRequestForm;
