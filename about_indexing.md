# Search engine indexing
## About this file
This file describes some theoretical background information about the indexing part of the search engine problem.
Hopefully this will help me decide later, which indexing method I should pursue.

## What is search engine indexing
Search engine indexing is the act of storing information about a document in such a way, that you can match that stored index with a query in the future for their similarity in a fast manner.
These are the two main criteria for a good index:
- The index should be small, thus making the matching part also fast
- The index should be representational for the full length document

### Forward index
The forward index is the mapping of a list of terms to their corresponding document. The list could look like this: 
```json
{
  "document_1": [
    {"term": "house", "count": 4},
    {"term": "car", "count": 9}
  ], "document_2": [
    {"term": "cat", "count": 5},
    {"term": "car", "count": 6}
  ]
}
```
The `count` property is how many times the term occurs in the document

### Reverse index
The reverse index is the mapping of documents to a term. The reverse index is created after creating the forward index list.
```json
{
  "house": {
    "tf_idf": 0.3,
    "documents": [
      "document_1"
    ]
  }, "car": {
    "tf_idf": 0,
    "documents": [
      "document_1",
      "document_2"
    ]
  }, "cat": {
    "tf_idf": 0.3,
    "documents": [
      "document_2"
    ]
  }
}
```

More information about idf can be found [here](https://github.com/fabianbaechli/sentiment_classification_with_tf/blob/master/sentiment_classification_with_tf/documentation/about_text_mining.md#idf)
