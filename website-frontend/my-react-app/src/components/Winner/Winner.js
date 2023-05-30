import Newgame from '../Newgame/Newgame'
import './Winner.css'

const Winner = ({numbers}) => {
    if (!numbers.every(n => n.value === n.index+1))
        return null
    return <div className='winner'>
                <p>You won!</p>
                <Newgame/>
            </div>
}

export default Winner