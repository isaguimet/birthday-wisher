import { useEffect, useState } from "react";
import axios from "axios";
import { Alert } from "reactstrap";
import BirthdayTable from "../components/birthdayTable/BirthdayTable"
import { StyledDiv } from "./WishingCenterPage.style";
import { useSelector } from "react-redux";

// filter out birthdays that have already past
function getUpcomingBirthdays(data) {
    const today = new Date();
    const day = today.getDate();
    const month = today.getMonth() + 1;
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



// const checkServerAvailable = () => {
//     for (let i = 0; i < servers.length; i++) {
//         axios.get(`http://localhost:${servers[i]}`)
//         .then((response) => {
            
//             return servers[i];
            
//         })
//         .catch((err) => {
//             console.log("HELLO");
//         })
//     }
//     return servers[0];
// }
// const s = checkServerAvailable();

const WishingCenterPage = () => {
    const [loading, setLoading] = useState(false);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);

    const userId = useSelector((state) => state.user.id);
    
    useEffect(() => {
        const servers = [8080, 8081, 8082];
        setLoading(true);
            axios.get(`http://localhost:${servers[0]}/users/friendList/6409806af230fe7750858163`).then((response) => {
                setLoading(false);
                setData(getUpcomingBirthdays(response.data.sort(compare)));
                console.log("response data: " + JSON.stringify(response.data))
                setError(null);
            }).catch((err) => {
                axios.get(`http://localhost:${servers[1]}/users/friendList/6409806af230fe7750858163`).then((response) => {
                    setLoading(false);
                    setData(getUpcomingBirthdays(response.data.sort(compare)));
                    console.log("response data: " + JSON.stringify(response.data))
                    setError(null);
                }).catch((err) => {
                    axios.get(`http://localhost:${servers[2]}/users/friendList/6409806af230fe7750858163`).then((response) => {
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
                });

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