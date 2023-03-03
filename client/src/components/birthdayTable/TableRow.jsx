
/**
 * A component for rendering a a row in a birthday table
 * @param props {Object} Expected props for this component:
 * - firstName {string} the first name of the friend
 * - lastName {string} the last name of the friend
 * - birthdate {string} the birthdate of the friend
 * @returns {JSX.Element}
 * @constructor
 */
const TableRow = (props) => {
    const { firstName, lastName, birthdate } = props
    var months = ["January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"];
    const month = months[parseInt(birthdate.slice(5, 7)) - 1];
    const day = birthdate.slice(8);

    return (
        <tr>
            <td>{month} {day}</td>
            <td>{firstName} {lastName}'s Birthday</td>
        </tr>
    )
}

export default TableRow;