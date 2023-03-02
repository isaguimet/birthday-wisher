import { useEffect, useState } from "react";
import axios from "axios";
import { Alert } from "reactstrap";
import BirthdayTable from "../components/birthdayTable/BirthdayTable"
import { StyledDiv } from "./WishingCenterPage.style";

// filter out birthdays that have already past
function getUpcomingBirthdays(data) {
    var today = new Date();
    var day = today.getDate();
    var month = today.getMonth() + 1;
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
    const [error, setError] = useState(null);

    const userId = "63f90f25dca51a6d00d65007";

    useEffect(() => {
        setLoading(true);
        axios.get(`http://localhost:8080/users/friendList/${userId}`).then((response) => {
            setLoading(false);
            setData(getUpcomingBirthdays(response.data.sort(compare)));
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
                </StyledDiv>
            ) : null}
        </div>
    );
};

export default WishingCenterPage;