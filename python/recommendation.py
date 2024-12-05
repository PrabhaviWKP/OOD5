from flask import Flask, request, jsonify
from surprise import dump

app = Flask(__name__)

# Load the trained model
_, algo = dump.load('data/model (1)/model')

@app.route('/recommend', methods=['POST'])
def recommend():
    data = request.json
    user_id = data['userID']

    # Get top-N recommendations for the user
    top_n = get_top_n(algo, user_id, n=5)

    return jsonify(top_n)


def str(i):
    pass


def range(param, param1):
    pass


def get_top_n(algo, user_id, n=10):
    # Get a list of all article IDs
    all_article_ids = [str(i) for i in range(1, 101)]  # Adjust based on your dataset

    # Get predictions for the user
    predictions = [algo.predict(user_id, article_id) for article_id in all_article_ids]

    # Sort predictions by estimated rating
    predictions.sort(key=lambda x: x.est, reverse=True)

    # Get the top-N recommendations
    top_n = [(pred.iid, pred.est) for pred in predictions[:n]]

    return top_n

if __name__ == '__main__':
    app.run(debug=True)
