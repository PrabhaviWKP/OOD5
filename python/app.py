from flask import Flask, request, jsonify
from transformers import pipeline

app = Flask(__name__)

# Load pre-trained text classification pipeline
classifier = pipeline("zero-shot-classification", model="facebook/bart-large-mnli")

# Define available categories
categories = ["Technology", "Health", "Sports", "AI", "Politics", "Entertainment"]

@app.route('/classify', methods=['POST'])
def classify_text():
    data = request.json
    text = data.get("text", "")
    if not text:
        return jsonify({"error": "No text provided"}), 400

    # Classify the text
    result = classifier(text, categories)
    top_category = result["labels"][0]
    return jsonify({"category": top_category})

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
