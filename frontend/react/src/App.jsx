import {Wrap,WrapItem,Button, Spinner} from "@chakra-ui/react";
import { ChakraProvider } from '@chakra-ui/react'
import SidebarWithHeader from "./components/shared/SideBar.jsx";
import {useEffect, useState} from "react";
import {getCustomers} from "./services/client.js";
import CardWithImage from "./components/Card.jsx";

const App = () => {

    const[customers, setCustomers] = useState([]);
    const[loading, setLoading] = useState(false);

    useEffect(() => {
        setLoading(true);
        //setTimeout(() => {

            getCustomers().then(res =>{
                // console.log(res.data);
                setCustomers(res.data);
            }).catch( err => {
                console.log(err);
            }).finally(() => {
                setLoading(false);
            })
        //}, 3000)


    },[])

    if(loading){
        return (
            <ChakraProvider>
                <SidebarWithHeader>
                    <Spinner
                        thickness='4px'
                        speed='0.65s'
                        emptyColor='gray.200'
                        color='blue.500'
                        size='xl'
                    />
                </SidebarWithHeader>

            </ChakraProvider>
        )
    }

    if(customers.length === 0){
        return (
            <SidebarWithHeader>
                <p>No customers found</p>
            </SidebarWithHeader>
        )
    }

    return (
        <ChakraProvider>
            <SidebarWithHeader>
                <Wrap justify={"center"} spacing={"30px"}>
                    {customers.map((customers, index) =>
                        // <p key={index}>{customers.name}</p>
                        <WrapItem key={index}>
                            <CardWithImage
                                {...customers}
                            />
                        </WrapItem>
                    )}
                    {/*<Button colorScheme='blue' variant='outline'>Button</Button>*/}
                </Wrap>
            </SidebarWithHeader>
        </ChakraProvider>
    )
}

export default App;