import '../../styles/school/SchoolOrder.css'

function RenderPage(){
    return (
        <h1 className={'d-flex w-100 justify-content-center'}>Coming soon</h1>
    )
}

export default function SchoolOrder(){
    document.title = 'Order'
    return (
        <RenderPage/>
    )
}