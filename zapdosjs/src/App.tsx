import React, {useEffect} from 'react';
import logo from './logo.svg';
import './App.css';
import {IMessageEvent, w3cwebsocket} from 'websocket';
// @ts-ignore
import P5Wrapper from 'react-p5-wrapper';
import MainSketch from "./Sketches/MainSketch";

const client = new w3cwebsocket('ws://127.0.0.1:8080/zetengine');

function App() {
    useEffect(() => {
        client.onopen = () => {
            console.log('WebSocket Client Connected');
        };
        client.onmessage = (message: IMessageEvent) => {
            console.log(message);
        };

    }, [])
    return (
        <div className="App">
            <header className="App-header">
                <img src={logo} className="App-logo" alt="logo"/>
                <P5Wrapper
                    sketch={MainSketch}
                    color={{color: [255, 0, 0]}}
                />
                <p>
                    Edit <code>src/App.tsx</code> and save to reload.
                </p>
                <a
                    className="App-link"
                    href="https://reactjs.org"
                    target="_blank"
                    rel="noopener noreferrer"
                >
                    Learn React
                </a>
                <button
                    onClick={() => {
                        //client.send("Pruebaaa :v ardilla");
                        const object = {
                            gameObjectId: "asdasdafsa",
                            componetType:"transformation",
                            componentProperties: {
                                position: {
                                    x: 1, y: 1, z: 1
                                }
                            }
                        };
                        client.send(
                            JSON.stringify(object)
                        );
                        console.log("HOLAAAAAA");
                    }}
                >
                    Soy Una ardilla
                </button>
            </header>
        </div>
    );
}

export default App;
