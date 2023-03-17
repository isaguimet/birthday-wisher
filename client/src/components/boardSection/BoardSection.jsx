import {useEffect, useState} from 'react';
import BirthdayBoard from "../birthdayBoard/BirthdayBoard";
import axios from "axios";
import {BoardContainer} from "../../pages/ProfilePage.style";
import {Alert} from "reactstrap";
import Container from "react-bootstrap/Container";
import Button from "@mui/material/Button";
import {useSelector} from "react-redux";

/**
 * A component for fetching and rendering information about all boards that belong to a specific user.
 * @param props {Object} Expected props for this component:
 * - profileUser {string} the unique identifier for the user whose boards this component should display.
 * @returns {JSX.Element}
 * @constructor
 */
const BoardSection = (props) => {

    const [loading, setLoading] = useState(false);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);

    const loggedInUser = useSelector((state) => state.user.id);
    const loggedInUserIsProfileUser = loggedInUser === props.profileUser;

    useEffect(() => {
        setLoading(true);
        axios.get(`http://localhost:8080/boards/byUserId/${props.profileUser}`).then((response) => {
            setLoading(false);
            setData(response.data);
            setError(null);
        }).catch((err) => {
            setLoading(false);
            //setData(null);
            if (err.response) {
                setError(err.response.data);
            } else {
                setError(err.message);
            }
        });
    }, []);

    const handleAlertToggle = () => {
        setError(null);
    };

    const createBoard = () => {
        const data = {
            userId: props.profileUser,
        };
        setLoading(true);
        axios.post("http://localhost:8080/boards", data).then((response) => {
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
        });
    };

    return (
        <BoardContainer>
            <Container>
                {!!error && (
                    <Alert color={"danger"} toggle={handleAlertToggle}>
                        Error fetching board info: {error}
                    </Alert>
                )}
                {loading && <div>Loading board info...</div>}
                {!loading && data ? <>
                    {loggedInUserIsProfileUser && (
                        <Button variant="contained" size="small" onClick={createBoard}>
                            New Board
                        </Button>
                    )}
                    <div>
                        {data.map((board) => (
                            <BirthdayBoard
                                key={board.id}
                                id={board.id}
                                boardId={board.id}
                                year={board.year}
                                open={board.open}
                                public={board.public}
                                messages={board.messages}
                                setLoading={setLoading}
                                setData={setData}
                                setError={setError}
                                profileUser={props.profileUser}
                            />
                        ))}
                    </div>
                </> : null}
            </Container>
        </BoardContainer>
    );
};

export default BoardSection;