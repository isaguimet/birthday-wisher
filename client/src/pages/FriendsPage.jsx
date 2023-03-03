import Theme from "../theme/Theme";
import FriendCard from "../components/friendCard/FriendCard";
import PendingFriendCard from "../components/pendingFriendCard/PendingFriendCard";
import Container from "react-bootstrap/Container";
import {useState} from "react";
import {Alert} from "reactstrap";
import {BsPersonPlusFill} from "react-icons/bs";
import SearchBar from "../components/searchBar/SearchBar";
import axios from "axios";

const FriendsPage = () => {
    const [loading, setLoading] = useState(false);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    const [status, setStatus] = useState(0)

    const [loadingForSendingRequest, setLoadingForSendingRequest] = useState(false);
    const [dataForSendingRequest, setDataForSendingRequest] = useState(null);
    const [errorForSendingRequest, setErrorForSendingRequest] = useState(null);

    const handleAlertToggle = () => {
        setError(null);
    }

    const handleClickToSendRequest = (props) => {
        // TODO: switch userEmail and friendEmail. Get userEmail of userId
        const queryParams = {
            'userEmail': data.email,
            'friendEmail': "abc@hotmail.com"
        }

        setLoadingForSendingRequest(true);
        axios.patch(`http://localhost:8080/users/friendRequest`, null, {params: queryParams})
            .then((response) => {
                setLoadingForSendingRequest(false);
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
                            {dataForSendingRequest && <div>Send request succcesfully sent!</div>}
                            {loadingForSendingRequest && <div>Sending a friend request ...</div>}
                        </>
                    ) : null}
                </Container>
                <Container style={{display: "flex"}}>
                    <Container>
                        <h2>Friends birthdays</h2>
                        <FriendCard/>
                    </Container>
                    <Container>
                        <PendingFriendCard/>
                    </Container>
                </Container>
            </Container>
        </Theme>
    );
};

export default FriendsPage;