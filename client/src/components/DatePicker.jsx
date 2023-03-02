/**
 * A component for requesting a date input from the user.
 * @param props {object} Expected props for this component:
 * - setDate {function} function to set the date in the state of a parent component.
 * @returns {JSX.Element}
 * @constructor
 */
const DatePicker = (props) => {

    const handleChange = (e) => {
        props.setDate(e.target.value);
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
                required
                onChange={handleChange}
            />
        </div>
    );
};

export default DatePicker;
