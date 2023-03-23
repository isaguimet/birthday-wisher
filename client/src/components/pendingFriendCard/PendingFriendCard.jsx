import {useEffect, useState} from "react";
import {Alert} from "reactstrap";
import FriendRequestCard from "../friendRequestCard/FriendRequestCard";
import axiosInstance from "../../utils/API";
import Container from "react-bootstrap/Container";

const PendingFriendCard = (props) => {

    const [error, setError] = useState(null);

    const handleAlertToggle = () => {
        setError(null);
    }

    useEffect(() => {
        props.setLoadingForPendingFriends(true);
        axiosInstance.get(`http://localhost:8080/users/pendingFriendRequests/${props.userId}`).then((response) => {
            props.setLoadingForPendingFriends(false);
            props.setDataForPendingFriends(response.data);
            setError(null);
        }).catch((err) => {
            axiosInstance.get(`http://localhost:8081/users/pendingFriendRequests/${props.userId}`).then((response) => {
                props.setLoadingForPendingFriends(false);
                props.setDataForPendingFriends(response.data);
                setError(null);
            }).catch((err) => {
                axiosInstance.get(`http://localhost:8082/users/pendingFriendRequests/${props.userId}`).then((response) => {
                    props.setLoadingForPendingFriends(false);
                    props.setDataForPendingFriends(response.data);
                    setError(null);
                }).catch((err) => {
                    props.setLoadingForPendingFriends(false);
                    if (err.response) {
                        setError(err.response.data);
                    } else {
                        setError(err.message);
                    }
                });
            });
        });
    }, []);

    return (
        <Container>
            <h2>Pending Friend Requests</h2>
            {!!error && (
                <Alert color={"danger"} toggle={handleAlertToggle}>
                    Error: {error}
                </Alert>
            )}
            {props.loadingForPendingFriends && <div>Loading pending friend requests...</div>}
            {!(props.loadingForPendingFriends) && props.dataForPendingFriends ? (
                <div>
                    {props.dataForPendingFriends.map((friendInfo) => (
                        <FriendRequestCard userId={props.userId}
                                           friendEmail={friendInfo.email}
                                           friendInfo={friendInfo}
                                           setDataForPendingFriends={props.setDataForPendingFriends}
                                           setLoadingForPendingFriends={props.setLoadingForPendingFriends}
                                           setErrorForPendingFriends={setError}
                                           setDataForFriends={props.setDataForFriends}/>
                    ))}
                </div>
            ) : null}
        </Container>
    );
}

export default PendingFriendCard;