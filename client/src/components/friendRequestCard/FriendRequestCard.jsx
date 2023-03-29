import axiosInstance from "../../utils/API";
import Card from "react-bootstrap/Card";
import button from "bootstrap/js/src/button";

const FriendRequestCard = (props) => {
    const {userId, friendEmail, friendInfo} = props;

    const handleAcceptClick = () => {

        const queryParams = {
            'userId': userId,
            'friendEmail': friendEmail
        }

        // accept the friend request (returns updated list of pending friends)
        props.setLoadingForPendingFriends(true);
        axiosInstance.patch(`http://localhost:8080/users/pendingFriendRequests/accept`, null, {params: queryParams}).then((response) => {
            const [updatedPending, updatedFriends] = response.data;
            props.setLoadingForPendingFriends(false);
            props.setDataForPendingFriends(updatedPending);
            props.setDataForFriends(updatedFriends);
            props.setErrorForPendingFriends(null);
        }).catch((err8080) => {
            if (err8080.response) {
                props.setLoadingForPendingFriends(false);
                props.setErrorForPendingFriends(err8080.response.data);
            } else {
                axiosInstance.patch(`http://localhost:8081/users/pendingFriendRequests/accept`, null, {params: queryParams}).then((response) => {
                    const [updatedPending, updatedFriends] = response.data;
                    props.setLoadingForPendingFriends(false);
                    props.setDataForPendingFriends(updatedPending);
                    props.setDataForFriends(updatedFriends);
                    props.setErrorForPendingFriends(null);
                }).catch((err) => {
                    props.setLoadingForPendingFriends(false);
                    if (err.response) {
                        props.setErrorForPendingFriends(err.response.data);
                    } else {
                        props.setErrorForPendingFriends(err.message);
                    }
                });
            }
        });
    }

    const handleDeclineClick = () => {

        const queryParams = {
            'userId': userId,
            'friendEmail': friendEmail
        }

        props.setLoadingForPendingFriends(true);
        axiosInstance.patch(`http://localhost:8080/users/pendingFriendRequests/decline`, null, {params: queryParams}).then((response) => {
                props.setLoadingForPendingFriends(false);
                props.setDataForPendingFriends(response.data);
                props.setErrorForPendingFriends(null);
        }).catch((err8080) => {
            if (err8080.response) {
                props.setLoadingForPendingFriends(false);
                props.setErrorForPendingFriends(err8080.response.data);
            } else {
                axiosInstance.patch(`http://localhost:8081/users/pendingFriendRequests/decline`, null, {params: queryParams}).then((response) => {
                    props.setLoadingForPendingFriends(false);
                    props.setDataForPendingFriends(response.data);
                    props.setErrorForPendingFriends(null);
                }).catch((err) => {
                    props.setLoadingForPendingFriends(false);
                    if (err.response) {
                        props.setErrorForPendingFriends(err.response.data);
                    } else {
                        props.setErrorForPendingFriends(err.message);
                    }
                });
            }
        });
    }

    return (
        <Card key={friendInfo.id} style={{width: '30rem'}}>
            <Card.Body>
                {Object.keys(friendInfo.pendingFriends).map((userId) => (
                    <div key={userId}>
                        {userId === props.userId ? (
                            <div>
                                {friendInfo.pendingFriends[userId] ? (
                                        <div style={{display: "flex", justifyContent: "space-between"}}>
                                            {friendInfo.firstName} {friendInfo.lastName}
                                            <button disabled>Friend request sent</button>
                                        </div>
                                    ) :
                                    (<div style={{display: "flex", justifyContent: "space-between"}}>
                                        {friendInfo.firstName} {friendInfo.lastName}
                                        <div>
                                            <button onClick={handleAcceptClick}>Accept</button>
                                            <button onClick={handleDeclineClick}>Decline</button>
                                        </div>
                                    </div>)}
                            </div>
                        ) : null}
                    </div>
                ))}
            </Card.Body>
        </Card>
    );
};

export default FriendRequestCard;