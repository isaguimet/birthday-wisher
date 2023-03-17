import Theme from "../theme/Theme";
import FriendCard from "../components/friendCard/FriendCard";
import PendingFriendCard from "../components/pendingFriendCard/PendingFriendCard";
import Container from "react-bootstrap/Container";
import {useState} from "react";
import {Alert} from "reactstrap";
import {BsPersonPlusFill} from "react-icons/bs";
import SearchBar from "../components/searchBar/SearchBar";
import axios from "axios";
import {useSelector} from "react-redux";

const FriendsPage = () => {
    const loggedInUser = useSelector((state) => state.user.id);
    const loggedInUserEmail = useSelector((state) => state.user.email);
    const [loading, setLoading] = useState(false);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    const [status, setStatus] = useState(0)

    const [loadingForSendingRequest, setLoadingForSendingRequest] = useState(false);
    const [dataForSendingRequest, setDataForSendingRequest] = useState(null);
    const [errorForSendingRequest, setErrorForSendingRequest] = useState(null);

    const [loadingForPendingFriends, setLoadingForPendingFriends] = useState(false);
    const [dataForPendingFriends, setDataForPendingFriends] = useState([]);
    const [errorForPendingFriends, setErrorForPendingFriends] = useState(null);

    const handleAlertToggle = () => {
        setError(null);
    }

    const handleClickToSendRequest = () => {
        const queryParams = {
            'userEmail': loggedInUserEmail,
            'friendEmail': data.email
        }

        setLoadingForSendingRequest(true);
        axios.patch(`http://localhost:8080/users/friendRequest`, null, {params: queryParams})
            .then((response) => {
                setLoadingForSendingRequest(false);
                // Adds a pending friend onto the old data list
                setDataForPendingFriends(oldData => [...oldData, response.data]);
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

    return (
        <Theme>
            <Container>
                <Container className="searchContainer">
                    <h1>Friends List</h1>
                    <SearchBar
                        setData={setData}
                        setError={setError}
                        setStatus={setStatus}
                        setLoading={setLoading}
                    />
                </Container>
                <Container className="searchResultContainer">
                    {!!error && (
                        <Alert color={"warning"} toggle={handleAlertToggle}>
                            Error: {error}
                        </Alert>
                    )}
                    {loading && <div>Finding user given this email ...</div>}
                    {!loading && data && !(status === 404) ? (
                        <>{data.firstName} {data.lastName}
                            <BsPersonPlusFill onClick={handleClickToSendRequest}/>
                            {!!errorForSendingRequest && <div>{errorForSendingRequest}</div>}
                            {dataForSendingRequest && <div>Send request successfully sent!</div>}
                            {loadingForSendingRequest && <div>Sending a friend request ...</div>}
                        </>
                    ) : null}
                </Container>
                <Container style={{display: "flex"}}>
                    <Container>
                        <h2>Friends birthdays</h2>
                        <FriendCard userId={loggedInUser}/>
                    </Container>
                    <Container>
                        <PendingFriendCard
                            userId={loggedInUser}
                            setData={setDataForPendingFriends}
                            data={dataForPendingFriends}
                            setError={setErrorForPendingFriends}
                            error={errorForPendingFriends}
                            setLoading={setLoadingForPendingFriends}
                            loading={loadingForPendingFriends}
                        />
                    </Container>
                </Container>
            </Container>
        </Theme>
    );
};

export default FriendsPage;