import Theme from "../theme/Theme";
import FriendCard from "../components/friendCard/FriendCard";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import {useState} from "react";
import {Alert} from "reactstrap";
import {BsPersonPlusFill} from "react-icons/bs";
import SearchBar from "../components/searchBar/SearchBar";

const FriendsPage = () => {
    const [loading, setLoading] = useState(false);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    const [status, setStatus] = useState(0)

    const handleAlertToggle = () => {
        setError(null);
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
                        <>{data.firstName} {data.lastName} <BsPersonPlusFill/></>
                    ) : null}
                </Container>
                <Container>
                    <Row>
                        <Col>
                            <h2>Friends birthdays</h2>
                            <FriendCard/>
                        </Col>
                        <Col>
                            <h2>Pending Friend Requests</h2>
                        </Col>
                    </Row>
                </Container>
            </Container>
        </Theme>
    );
};

export default FriendsPage;