import {useState} from "react";
import axiosInstance from "../../utils/API";

const FriendRequestCard = (props) => {
    const {userId, friendEmail} = props;

    const [loading, setLoading] = useState(false);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);

    // TODO: handle error & loading state...
    const handleAlertToggle = () => {
        setError(null);
    }

    const handleAcceptClick = () => {

        const queryParams = {
            'userId': userId,
            'friendEmail': friendEmail
        }

        setLoading(true);
        axiosInstance.patch(`users/pendingFriendRequests/accept`, null, {params: queryParams})
            .then((response) => {
            setLoading(false);
            setData(response.data);
            setError(null);
        }).catch((err) => {
            setLoading(false);
            if (err.response) {
                setError(err.response.data);
            } else {
                setError(err.message);
            }
            alert(error)
        });
    }

    const handleDeclineClick = () => {

        const queryParams = {
            'userId': userId,
            'friendEmail': friendEmail
        }

        setLoading(true);
        axiosInstance.patch(`users/pendingFriendRequests/decline`, null, {params: queryParams})
            .then((response) => {
                setLoading(false);
                setData(response.data);
                setError(null);
            }).catch((err) => {
            setLoading(false);
            if (err.response) {
                setError(err.response.data);
            } else {
                setError(err.message);
            }
            alert(error)
        });
    }

    return (
        <div>
            <button onClick={handleAcceptClick}>Accept</button>
            <button onClick={handleDeclineClick}>Decline</button>
        </div>
    );
};

export default FriendRequestCard;