# multiAgents.py
# --------------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
#
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


from util import manhattanDistance
from game import Directions
import random, util

from game import Agent

class ReflexAgent(Agent):
    """
    A reflex agent chooses an action at each choice point by examining
    its alternatives via a state evaluation function.

    The code below is provided as a guide.  You are welcome to change
    it in any way you see fit, so long as you don't touch our method
    headers.
    """


    def getAction(self, gameState):
        """
        You do not need to change this method, but you're welcome to.

        getAction chooses among the best options according to the evaluation function.

        Just like in the previous project, getAction takes a GameState and returns
        some Directions.X for some X in the set {NORTH, SOUTH, WEST, EAST, STOP}
        """
        # Collect legal moves and successor states
        legalMoves = gameState.getLegalActions()

        # Choose one of the best actions
        scores = [self.evaluationFunction(gameState, action) for action in legalMoves]
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices) # Pick randomly among the best

        "Add more of your code here if you want to"

        return legalMoves[chosenIndex]

    def evaluationFunction(self, currentGameState, action):
        """
        Design a better evaluation function here.

        The evaluation function takes in the current and proposed successor
        GameStates (pacman.py) and returns a number, where higher numbers are better.

        The code below extracts some useful information from the state, like the
        remaining food (newFood) and Pacman position after moving (newPos).
        newScaredTimes holds the number of moves that each ghost will remain
        scared because of Pacman having eaten a power pellet.

        Print out these variables to see what you're getting, then combine them
        to create a masterful evaluation function.
        """
        # Useful information you can extract from a GameState (pacman.py)
        successorGameState = currentGameState.generatePacmanSuccessor(action)
        newPos = successorGameState.getPacmanPosition()
        newFood = successorGameState.getFood()
        newGhostStates = successorGameState.getGhostStates()
        newScaredTimes = [ghostState.scaredTimer for ghostState in newGhostStates]

        "*** YOUR CODE HERE ***"
        score = successorGameState.getScore()
        foodList = newFood.asList() 
        
        # nearest food score
        if foodList:
            minDistance = min((manhattanDistance(newPos,food)) for food in foodList)
            score += (1/minDistance)
                

        #nearest ghost
        for ghost in newGhostStates:
            ghostPos = ghost.getPosition()
            ghostDistance = manhattanDistance(newPos, ghostPos)
            if ghostDistance > 0:
                if ghostDistance <= 2:     
                    return -1
                else:
                    score -= 2 / ghostDistance
        return score


def scoreEvaluationFunction(currentGameState):
    """
    This default evaluation function just returns the score of the state.
    The score is the same one displayed in the Pacman GUI.

    This evaluation function is meant for use with adversarial search agents
    (not reflex agents).
    """
    return currentGameState.getScore()

class MultiAgentSearchAgent(Agent):
    """
    This class provides some common elements to all of your
    multi-agent searchers.  Any methods defined here will be available
    to the MinimaxPacmanAgent, AlphaBetaPacmanAgent & ExpectimaxPacmanAgent.

    You *do not* need to make any changes here, but you can if you want to
    add functionality to all your adversarial search agents.  Please do not
    remove anything, however.

    Note: this is an abstract class: one that should not be instantiated.  It's
    only partially specified, and designed to be extended.  Agent (game.py)
    is another abstract class.
    """

    def __init__(self, evalFn = 'scoreEvaluationFunction', depth = '2'):
        self.index = 0 # Pacman is always agent index 0
        self.evaluationFunction = util.lookup(evalFn, globals())
        self.depth = int(depth)

class MinimaxAgent(MultiAgentSearchAgent):
    """
    Your minimax agent (question 2)
    """

    def getAction(self, gameState):
        """
        Returns the minimax action from the current gameState using self.depth
        and self.evaluationFunction.

        Here are some method calls that might be useful when implementing minimax.

        gameState.getLegalActions(agentIndex):
        Returns a list of legal actions for an agent
        agentIndex=0 means Pacman, ghosts are >= 1

        gameState.generateSuccessor(agentIndex, action):
        Returns the successor game state after an agent takes an action

        gameState.getNumAgents():
        Returns the total number of agents in the game

        gameState.isWin():
        Returns whether or not the game state is a winning state

        gameState.isLose():
        Returns whether or not the game state is a losing state
        """
        "*** YOUR CODE HERE ***"
    def getAction(self, gameState):
        legalActions = gameState.getLegalActions(0)
        bestAction = "Stop"
        bestScore = -999999

        for action in legalActions:
            successor = gameState.generateSuccessor(0, action)
            score = self.min(successor, self.depth, 1)
            if score > bestScore:
                bestScore = score
                bestAction = action
        
        return bestAction

    def max(self, state, depth):
        if state.isWin() or state.isLose() or depth == 0:
            return self.evaluationFunction(state)

        v = -999999
        actions = state.getLegalActions(0)
        for action in actions:
            successor = state.generateSuccessor(0, action)
            score = self.min(successor, depth, 1)
            if score > v:
                v = score
        return v

    def min(self, state, depth, agentIndex):
        if state.isWin() or state.isLose() or depth == 0:
            return self.evaluationFunction(state)

        v = 999999
        actions = state.getLegalActions(agentIndex)
        
        numAgents = state.getNumAgents()
        nextAgent = agentIndex + 1
        nextDepth = depth

        if nextAgent == numAgents:
            nextAgent = 0
            nextDepth = depth - 1

        for action in actions:
            successor = state.generateSuccessor(agentIndex, action)
            if nextAgent == 0:
                score = self.max(successor, nextDepth)
            else:
                score = self.min(successor, nextDepth, nextAgent)
            
            if score < v:
                v = score
        return v


class AlphaBetaAgent(MultiAgentSearchAgent):
    """
    Your minimax agent with alpha-beta pruning (question 3)
    """
    
    
    def getAction(self, gameState):
        """
        Returns the minimax action using self.depth and self.evaluationFunction
        """
        "*** YOUR CODE HERE ***"

            
       
        alpha = -9999
        beta = 99999
        legalActions = gameState.getLegalActions(0)
        bestAction = "Stop"
        bestScore = -99999

        for action in legalActions:
            successor = gameState.generateSuccessor(0, action)
            score = self.min(successor, self.depth, 1, alpha, beta)
            if score > bestScore:
                bestScore = score
                bestAction = action
            if bestScore > beta:
                return bestAction
            alpha = max(alpha, bestScore)
        return bestAction
    
    def max(self, state, depth, alpha, beta):
        if state.isWin() or state.isLose() or depth == 0:
            return self.evaluationFunction(state)
        value = -99999
        for action in state.getLegalActions(0):
            successor = state.generateSuccessor(0, action)
            value = max(value, self.min(successor, depth, 1, alpha, beta))
            if value > beta:
                return value
            alpha = max(alpha, value)
        return value
    
    def min(self, state, depth, agentIndex, alpha, beta):
        if state.isWin() or state.isLose() or depth == 0:
            return self.evaluationFunction(state)

        value = 9999
        nextAgent = agentIndex + 1
        nextDepth = depth

        if nextAgent == state.getNumAgents():
            nextAgent = 0
            nextDepth = depth - 1

        for action in state.getLegalActions(agentIndex):
            successor = state.generateSuccessor(agentIndex, action)
            if nextAgent == 0:
                score = self.max(successor, nextDepth, alpha, beta)
            else:
                score = self.min(successor, nextDepth, nextAgent, alpha, beta)
            
            value = min(value, score)
            if value < alpha:
                return value
            beta = min(beta, value)
        return value


class ExpectimaxAgent(MultiAgentSearchAgent):
    """
      Your expectimax agent (question 4)
    """

    def getAction(self, gameState):
        """
        Returns the expectimax action using self.depth and self.evaluationFunction

        All ghosts should be modeled as choosing uniformly at random from their
        legal moves.
        """
        "*** YOUR CODE HERE ***"
     

        legalActions = gameState.getLegalActions(0)
        bestAction = "Stop"
        bestScore = -99999

        for action in legalActions:
            successor = gameState.generateSuccessor(0, action)
            score = self.expectation(successor, self.depth,1)
            if score > bestScore:
                bestScore = score
                bestAction = action
            
        return bestAction

    def max(self,state,depth):
        if state.isWin() or state.isLose() or depth == 0:
            return self.evaluationFunction(state)


        value = -9999
        actions = state.getLegalActions(0)
        for action in actions:
            successor = state.generateSuccessor(0,action)
            value = max(value,self.expectation(successor,depth,1))
        return value 

    def expectation(self, state,depth,agentIndex):
        if state.isWin() or state.isLose() or depth == 0:
            return self.evaluationFunction(state)
        
        value =0
        actions = state.getLegalActions(agentIndex)
        depth = depth
        if agentIndex + 1 == state.getNumAgents():
            next = 0
            depth = depth - 1
        else:
            next = agentIndex + 1
            depth = depth

        for action in actions:
            successor = state.generateSuccessor(agentIndex, action)
            if next == 0:
                score = self.max(successor, depth)
            else:
                score = self.expectation(successor, depth, next)
            
            value += (1/len(actions)) * score
            
        return value



def betterEvaluationFunction(currentGameState):
    """
    Your extreme ghost-hunting, pellet-nabbing, food-gobbling, unstoppable
    evaluation function (question 5).

    DESCRIPTION:I essentially used my old evaluation function (chase down the nearest food, avoid ghosts), but thought to add in ghost hunting as well. 
    """
    "*** YOUR CODE HERE ***"
    pos = currentGameState.getPacmanPosition()
    food = currentGameState.getFood()
    ghostStates = currentGameState.getGhostStates()
    score = currentGameState.getScore()
    
    
    foodList = food.asList()
        # nearest food score
    if foodList:
        minDistance = min((manhattanDistance(pos,food)) for food in foodList)    
        score += (1/minDistance)

    for ghost in ghostStates:
        dist = manhattanDistance(pos, ghost.getPosition())
        
        if ghost.scaredTimer > 0:
            score += 200.0 / (dist + 1)
        else:
            if dist < 2:   
                score -= 99999
            elif dist > 0:
                score -= 1.1 / dist

    score -= 5 * len(foodList)

    return score
# Abbreviation
better = betterEvaluationFunction
