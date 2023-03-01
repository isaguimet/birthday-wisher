import React, { useRef, useState } from 'react';

const DatePicker = () => {
    const [date, setDate] = useState('');
    const dateInputRef = useRef(null);

    const handleChange = (e) => {
        setDate(e.target.value);
    };
    
    return (
    //Minimum year: 0000 01 01
    //Maximum year: 3000 12 31
    <div>
        <input
        type="date"
        min="0000-01-01"
        max="3000-12-31"
        className="form-control mt-1"
        onChange={handleChange}
        ref={dateInputRef}
        />
    </div>
);
};

export default DatePicker;
