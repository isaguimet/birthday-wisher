import {Board, BoardContainer} from "../../pages/ProfilePage.style";
import BirthdayCard from "../birthdayCard/BirthdayCard";
import BootstrapSwitchButton from "bootstrap-switch-button-react";
import {useState} from "react";
import axios from "axios";

/**
 * A component for rendering a User Birthday Board.
 * @param props {Object} Expected props for this component:
 * - boardId {string} the unique identifier for this board in the database.
 * - year {string} the year that this board is for.
 * - open {boolean} whether the board is open or closed (for people to submit messages to).
 * - public {boolean} whether the board is public or private (visibility from friends).
 * - messages {object} birthday messages that belong to this board.
 * - setLoading {function} function to set loading in a parent component.
 * - setData {function} function to set data in a parent component.
 * - setError {function} function to set error in a parent component.
 * @returns {JSX.Element}
 * @constructor
 */
const BirthdayBoard = (props) => {
    const [isOpen, setOpen] = useState(props.open === true);
    const [isPublic, setPublic] = useState(props.public === true);

    const togglePublic = () => {
        props.setLoading(true);
        if (isPublic) {
            axios.patch(`http://localhost:8080/boards/setPrivate/${props.boardId}`).then((response) => {
                props.setLoading(false);
                props.setData(response.data);
                props.setError(null);
                setPublic(false);
            }).catch((err) => {
                props.setLoading(false);
                props.setData(null);
                props.setError(err.response.statusText);
            });
        } else {
            axios.patch(`http://localhost:8080/boards/setPublic/${props.boardId}`).then((response) => {
                props.setLoading(false);
                props.setData(response.data);
                props.setError(null);
                setPublic(true);
            }).catch((err) => {
                props.setLoading(false);
                props.setData(null);
                props.setError(err.response.statusText);
            });
        }
    };

    const toggleOpen = () => {
        props.setLoading(true);
        if (isOpen) {
            axios.patch(`http://localhost:8080/boards/setClosed/${props.boardId}`).then((response) => {
                props.setLoading(false);
                props.setData(response.data);
                props.setError(null);
                setOpen(false);
            }).catch((err) => {
                props.setLoading(false);
                props.setData(null);
                props.setError(err.response.statusText);
            });
        } else {
            axios.patch(`http://localhost:8080/boards/setOpen/${props.boardId}`).then((response) => {
                props.setLoading(false);
                props.setData(response.data);
                props.setError(null);
                setOpen(true);
            }).catch((err) => {
                props.setLoading(false);
                props.setData(null);
                props.setError(err.response.statusText);
            });
        }
    };

    const deleteBoard = () => {
        props.setLoading(true);
        axios.delete(`http://localhost:8080/boards/${props.boardId}`).then((response) => {
            props.setLoading(false);
            props.setData(response.data);
            props.setError(null);
        }).catch((err) => {
            props.setLoading(false);
            props.setData(null);
            props.setError(err.response.statusText);
        });
    };

    return (
        <BoardContainer>
            {props.year}

            {/*TODO: only make these switches visible if this is viewed on the profile page of the current user*/}
            <BootstrapSwitchButton
                id={"public_toggle"}
                checked={isPublic}
                onlabel={"Public"}
                offlabel={"Private"}
                onChange={togglePublic}
                width={100}
            />
            <BootstrapSwitchButton
                id={"open_toggle"}
                checked={isOpen}
                onlabel={"Open"}
                offlabel={"Closed"}
                onChange={toggleOpen}
                width={100}
            />

            <button onClick={deleteBoard}>Delete Board</button>
            <button onClick={() => {
            }}>Add Wish
            </button>

            {/* if no item it doesn't render anything, if no item it should just render blank */}
            <Board>
                {Object.entries(props.messages).map(([msgId, msg]) => (
                    <BirthdayCard
                        key={msgId}
                        id={msgId}
                        boardId={props.boardId}
                        msgId={msgId}
                        fromUserId={msg.fromUserId}
                        toUserId={msg.toUserId}
                        lastUpdatedDate={msg.lastUpdatedDate}
                        msgText={msg.msgText}
                        setLoading={props.setLoading}
                        setData={props.setData}
                        setError={props.setError}
                    />
                ))}
            </Board>
        </BoardContainer>
    );
};

export default BirthdayBoard;