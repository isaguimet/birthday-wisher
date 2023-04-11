import Theme from "../theme/Theme";
import PendingFriendCard from "../components/pendingFriendCard/PendingFriendCard";
import Container from "react-bootstrap/Container";
import {useState} from "react";
import {Alert} from "reactstrap";
import {BsPersonPlusFill} from "react-icons/bs";
import SearchBar from "../components/searchBar/SearchBar";
import {useSelector} from "react-redux";
import axiosInstance from "../utils/API";
import FriendBirthdayList from "../components/friendBirthdayList/FriendBirthdayList";

const FriendsPage = () => {
    const loggedInUser = useSelector((state) => state.user.id);
    const loggedInUserEmail = useSelector((state) => state.user.email);
    const [loading, setLoading] = useState(false);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);

    const [loadingForSendingRequest, setLoadingForSendingRequest] = useState(false);
    const [dataForSendingRequest, setDataForSendingRequest] = useState(null);
    const [errorForSendingRequest, setErrorForSendingRequest] = useState(null);

    const [loadingForPendingFriends, setLoadingForPendingFriends] = useState(false);
    const [dataForPendingFriends, setDataForPendingFriends] = useState([]);

    const [loadingForFriends, setLoadingForFriends] = useState(false);
    const [dataForFriends, setDataForFriends] = useState(null);
    const [errorForFriends, setErrorForFriends] = useState(null);

    const handleAlertToggle = () => {
        setError(null);
    }

    const handleClickToSendRequest = () => {
        const queryParams = {
            'userEmail': loggedInUserEmail,
            'friendEmail': data.email
        }

        setLoadingForSendingRequest(true);
        axiosInstance.patch(`https://proxy1-08e6.onrender.com/users/friendRequest`, null, {params: queryParams}).then((response) => {
            setLoadingForSendingRequest(false);
            // Adds a pending friend onto the old data list
            setDataForPendingFriends(response.data);
            setDataForSendingRequest(response.data);
            setErrorForSendingRequest(null);
        }).catch((err8080) => {
            if (err8080.response) {
                setLoadingForSendingRequest(false);
                setErrorForSendingRequest(err8080.response.data);
            } else {
                axiosInstance.patch(`https://proxy2-no3f.onrender.com/users/friendRequest`, null, {params: queryParams}).then((response) => {
                    setLoadingForSendingRequest(false);
                    // Adds a pending friend onto the old data list
                    setDataForPendingFriends(response.data);
                    setDataForSendingRequest(response.data);
                    setErrorForSendingRequest(null);
                }).catch((err) => {
                    setLoadingForSendingRequest(false);
                    if (err.response) {
                        setErrorForSendingRequest(err.response.data);
                    } else {
                        setErrorForSendingRequest(err.message);
                    }
                });
            }
        });
    }

    return (
        <Theme>
            <Container>
                <Container className="searchContainer">
                    <h1>Friends List</h1>
                    <SearchBar
                        setData={setData}
                        setError={setError}
                        setLoading={setLoading}
                    />
                </Container>
                <Container className="searchResultContainer">
                    {!!error && (
                        <Alert color={"danger"} toggle={handleAlertToggle}>
                            Error: {error}
                        </Alert>
                    )}
                    {loading && <div>Finding user given this email ...</div>}
                    {!loading && data && !error ? (
                        <>{data.firstName} {data.lastName}
                            <BsPersonPlusFill onClick={handleClickToSendRequest}/>
                            {!!errorForSendingRequest && <div>{errorForSendingRequest}</div>}
                            {dataForSendingRequest && <div>Send request successfully sent!</div>}
                            {loadingForSendingRequest && <div>Sending a friend request ...</div>}
                        </>
                    ) : null}
                </Container>
                <Container style={{display: "flex"}}>
                    <FriendBirthdayList
                        loggedInUser={loggedInUser}
                        data={dataForFriends}
                        setData={setDataForFriends}
                        loading={loadingForFriends}
                        setLoading={setLoadingForFriends}
                        error={errorForFriends}
                        setError={setErrorForFriends}
                    />

                    <PendingFriendCard
                        userId={loggedInUser}
                        setDataForPendingFriends={setDataForPendingFriends}
                        dataForPendingFriends={dataForPendingFriends}
                        setLoadingForPendingFriends={setLoadingForPendingFriends}
                        loadingForPendingFriends={loadingForPendingFriends}
                        setDataForFriends={setDataForFriends}
                    />
                </Container>
            </Container>
        </Theme>
    );
};

export default FriendsPage;