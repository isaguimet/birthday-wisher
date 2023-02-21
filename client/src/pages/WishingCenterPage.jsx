import "./WishingCenterPage.css";
import Table from 'react-bootstrap/Table';

const WishingCenterPage = () => {

    return (
        <div className="wishing-center-container">
            <h1>Upcoming Birthdays</h1>
            <Table striped bordered hover>
                <tbody>
                    <tr>
                        <td>February 12</td>
                        <td>John's Birthday</td>
                    </tr>
                    <tr>
                        <td>February 23</td>
                        <td>Tom's Birthday</td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>Lisa's Birthday</td>
                    </tr>
                    <tr>
                        <td>March 14</td>
                        <td>Bobby's Birthday</td>
                    </tr>
                    <tr>
                        <td>March 26</td>
                        <td>Alice's Birthday</td>
                    </tr>
                </tbody>
            </Table>
        </div>
    );
};

export default WishingCenterPage;