import TableRow from "./TableRow"
import Table from 'react-bootstrap/Table';

/**
 * A component for rendering a table of upcoming friends' birthdays 
 * @param props {Object} Expected props for this component:
 * - data {ObjectId} the list of friends
 * @returns {JSX.Element}
 * @constructor
 */
const BirthdayTable = (props) => {
    const { data } = props
    return (
        <div>
            <Table striped bordered hover>
                <tbody>
                    {data.map(row =>
                        <TableRow firstName={row.firstName}
                            lastName={row.lastName}
                            birthdate={row.birthdate} />
                    )}
                </tbody>
            </Table>
        </div>
    )
}

export default BirthdayTable;