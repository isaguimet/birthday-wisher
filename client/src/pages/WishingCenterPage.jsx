import { useEffect, useState } from "react";
import axios from "axios";
import { Alert } from "reactstrap";
import Table from 'react-bootstrap/Table';
import { StyledDiv } from "./WishingCenterPage.style";

const Row = (props) => {
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

const BirthdayTable = (props) => {
    const { data } = props
    return (
        <div>
            <Table striped bordered hover>
                <tbody>
                    {data.map(row =>
                        <Row firstName={row.firstName}
                            lastName={row.lastName}
                            birthdate={row.birthdate} />
                    )}
                </tbody>
            </Table>
        </div>
    )
}

function filterUpcomingBirthdays(data) {
    var today = new Date();
    var day = today.getDate();
    var month = today.getMonth() + 1;
    data.map(friend => console.log(" " + friend.birthdate.slice(5, 7) + " " + friend.birthdate.slice(8)));
    return data.filter(friend => parseInt(friend.birthdate.slice(5, 7)) > month || (parseInt(friend.birthdate.slice(5, 7)) === month && parseInt(friend.birthdate.slice(8))) >= day);
}

// comparator to sort birthdays in ascending order by day and month
function compare(a, b) {
    if (a.birthdate.slice(5, 7) < b.birthdate.slice(5, 7)) {
        return -1;
    } if (a.birthdate.slice(5, 7) > b.birthdate.slice(5, 7)) {
        return 1;
    } if (a.birthdate.slice(5, 7) === b.birthdate.slice(5, 7)) {
        if (a.birthdate.slice(8) < b.birthdate.slice(8)) {
            return -1;
        } if (a.birthdate.slice(8) > b.birthdate.slice(8)) {
            return 1;
        }
        return 0;
    }
}

const WishingCenterPage = () => {
    const [loading, setLoading] = useState(false);
    const [data, setData] = useState(null);
    // const [nextyearBirthdays, setNextYearBirthdays] = useState(null);
    const [error, setError] = useState(null);

    const userId = "63f90f25dca51a6d00d65007";

    useEffect(() => {
        setLoading(true);
        axios.get(`http://localhost:8080/users/friendList/${userId}`).then((response) => {
            setLoading(false);
            setData(filterUpcomingBirthdays(response.data.sort(compare)));
            // setNextYearBirthdays(response.data.sort(compare));
            console.log("response data: " + JSON.stringify(response.data))
            setError(null);
        }).catch((err) => {
            setLoading(false);
            if (err.response) {
                setError(err.response.data);
            } else {
                setError(err.message);
            }
            console.log(error)
        });
    }, []);

    const handleAlertToggle = () => {
        setError(null);
    }

    return (
        <div>
            {!!error && (
                <Alert color={"danger"} toggle={handleAlertToggle}>
                    Error: {error}
                </Alert>
            )}
            {loading && <div>Loading Wishing Center...</div>}
            {!loading && data ? (
                <StyledDiv>
                    <h1>Upcoming Birthdays</h1>
                    <BirthdayTable data={data} />
                    {/* <h1> Next Year Birthdays</h1>
                    <BirthdayTable data={nextyearBirthdays} /> */}
                </StyledDiv>
            ) : null}
        </div>
    );
};

export default WishingCenterPage;