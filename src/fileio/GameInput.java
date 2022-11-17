package fileio;

import java.util.ArrayList;

public final class GameInput {
        private StartGameInput startGame;
        private ArrayList<ActionsInput> actions;
        private int endTurn;
        private int playerTurn;
        public GameInput() {
        }

        public StartGameInput getStartGame() {
                return startGame;
        }

        public void setStartGame(final StartGameInput startGame) {
                this.startGame = startGame;
        }

        public ArrayList<ActionsInput> getActions() {
                return actions;
        }

        public void setActions(final ArrayList<ActionsInput> actions) {
                this.actions = actions;
        }

        public int getEndTurn() {
                return endTurn;
        }

        public void setEndTurn(final int endTurn) {
                this.endTurn = endTurn;
        }

        public int getPlayerTurn() {
                return playerTurn;
        }

        public void setPlayerTurn(final int playerTurn) {
                this.playerTurn = playerTurn;
        }

        @Override
        public String toString() {
                return "GameInput{"
                        +  "startGame="
                        + startGame
                        + ", actions="
                        + actions
                        + '}';
        }
}
