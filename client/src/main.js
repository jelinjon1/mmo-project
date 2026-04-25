import * as Phaser from "phaser";
import GameScene from "./scenes/GameScene.js";

const config = {
    type: Phaser.AUTO,
    width: 800,
    height: 600,
    scene: [GameScene],
};

new Phaser.Game(config);
