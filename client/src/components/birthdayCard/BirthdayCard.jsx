import Card from 'react-bootstrap/Card';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import {useState} from "react";
import {updateMsg} from "../../store/board";
import {useDispatch} from "react-redux";

/**
 * A component for rendering a User Birthday Message.
 * @param props {Object} Expected props for this component:
 * - boardId {string} the unique identifier for the board this message belongs to in the database.
 * - msgId {string} the unique identifier for this message in the database.
 * - fromUserId {string} the ID of the User who the message is from.
 * - toUserId {string} the ID of the User who the message is to.
 * - lastUpdatedDate {string} the date on which the message was last updated.
 * - msgText {string} the text content of the message.
 * @returns {JSX.Element}
 * @constructor
 */
const BirthdayCard = (props) => {
    const [isEditing, setEditing] = useState(false);
    const [input, setInput] = useState("");

    const dispatch = useDispatch();

    const handleChange = (event) => {
        setInput(event.target.value);
    };

    const handleSubmit = (event) => {
        console.log(`update msg ${props.msgId} to ${input}`);
        const body = {msgText: input};
        const data = {boardId: props.boardId, msgId: props.msgId, body};
        dispatch(updateMsg(data));
    }

    return (
        <Card style={{ width: '18rem' }}>
        <Card.Body>
            {!isEditing && <EditOutlinedIcon onClick={()=>setEditing(true)}/>}
            {isEditing && (
                <form onSubmit={handleSubmit}>
                    <input type={"text"} value={input} onChange={handleChange}/>
                    <input type={"submit"} value={"Submit"}/>
                </form>
            )}
            <Card.Text>
            {props.msgText}
            </Card.Text>
        </Card.Body>
        </Card>
    );

};

export default BirthdayCard