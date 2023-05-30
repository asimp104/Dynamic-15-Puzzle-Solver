import './Newgame.css'

const Newgame = ({reset}) => 
    <div className='button-wrapper'>
        <button onClick={reset}>New game?</button>
    </div>

export default Newgame