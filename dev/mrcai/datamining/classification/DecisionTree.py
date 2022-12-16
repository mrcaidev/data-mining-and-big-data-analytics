import numpy as np
from numpy.typing import NDArray
from typing import TypedDict
from typing_extensions import Self
from json import dumps


# Training dataset.
train_data = np.loadtxt("data/classification/train.txt")

# Testing dataset.
test_data = np.loadtxt("data/classification/test.txt")

# Dataset type.
Dataset = NDArray[np.float64]


class Leaf(TypedDict):
    """
    A leaf node on decision tree.
    """

    # The class label, 1, 2, 3 in this particular case.
    label: int


class Node(TypedDict):
    """
    A non-leaf node on decision tree.
    """

    # The feature to split on.
    feature: int

    # The threshold to split on.
    threshold: float

    # The children nodes.
    children: list[Self | Leaf]


def entropy(probabilities: list[float]) -> float:
    """
    Get the entropy of a list of probabilities.

    Args:
        probabilities: A list of probabilities.

    Returns:
        The entropy of the list of probabilities.
    """
    return sum([-p * np.log2(p) if p > 0 else 0 for p in probabilities])


def get_whole_entropy(dataset: Dataset) -> float:
    """
    Get the entropy of the whole dataset.

    Args:
        dataset: The dataset to calculate entropy on.

    Returns:
        The entropy of the whole dataset.
    """
    # Count the existence of each label.
    label_count = [0 for _ in range(3)]
    for row in dataset:
        label = int(row[-1]) - 1
        label_count[label] += 1

    # Calculate the whole entropy.
    row_count = dataset.shape[0]
    return entropy([count / row_count for count in label_count])


def get_feature_entropy(dataset: Dataset, feature_index: int) -> tuple[float, float]:
    """
    Get the entropy of a feature.

    Args:
        dataset: The dataset to calculate entropy on.
        feature_index: The column index of the feature in the dataset.

    Returns:
        The entropy of the feature, and the corresponding threshold.
    """
    row_count = dataset.shape[0]

    # Get all possible classifications.
    feature_values = np.unique(dataset[:, feature_index])
    thresholds = [
        np.round((feature_values[i + 1] + feature_values[i]) / 2, 2)
        for i in range(len(feature_values) - 1)
    ]

    # Find the minimal entropy among all classifications.
    min_entropy = float("inf")
    best_threshold = 0
    for threshold in thresholds:
        # |  side  |  case_count  | side_count |
        # | <=5.55 | 25 | 7  | 0  |     32     |
        # | >5.55  | 0  | 18 | 25 |     43     |
        case_count = [[0 for _ in range(3)] for _ in range(2)]
        for row in dataset:
            label = int(row[-1]) - 1
            side = 0 if row[feature_index] <= threshold else 1
            case_count[side][label] += 1

        side_count = [sum(case) for case in case_count]

        # Calculate the entropy of this classification.
        feature_entropy = sum(
            [
                side_count[i]
                / row_count
                * entropy([case_count[i][j] / side_count[i] for j in range(3)])
                for i in range(2)
            ]
        )

        # Update minimal entropy.
        if feature_entropy < min_entropy:
            min_entropy = feature_entropy
            best_threshold = threshold

    return float(min_entropy), float(best_threshold)


def get_best_feature(dataset: Dataset) -> tuple[int, float]:
    """
    Get the best feature to split on.

    Args:
        dataset: The dataset to split.

    Returns:
        The index of the best feature to split on,
        and the corresponding threshold.

    Note:
        Since the whole entropy is the same for all features,
        we only need to choose the feature with the minimal entropy,
        which is thus the feature with the maximal information gain.
    """
    min_entropy = float("inf")
    best_feature = 0
    best_threshold = 0
    for i in range(dataset.shape[1] - 1):
        entropy, threshold = get_feature_entropy(dataset, i)
        if entropy < min_entropy:
            min_entropy = entropy
            best_feature = i
            best_threshold = threshold

    return best_feature, best_threshold


def is_pure_side(side: list[int]) -> bool:
    """
    Check if a side is pure,
    which means every cases in this side have the same label.

    Args:
        side: A list of cases of a feature under a certain label and threshold.

    Returns:
        True if the side is pure, or False otherwise.

    Example:
        [0, 0, 25] is pure, while [0, 1, 24] is not.
    """
    return side.count(0) == len(side) - 1


def create_decision_tree(dataset: Dataset) -> Node:
    """
    Create a decision tree from a dataset.

    Args:
        dataset: The dataset to create a decision tree from.

    Returns:
        A decision tree.
    """
    feature_index, threshold = get_best_feature(dataset)

    case_count = [[0 for _ in range(3)] for _ in range(2)]
    data_subsets = [[], []]
    for row in dataset:
        label = int(row[-1]) - 1
        side = 0 if row[feature_index] <= threshold else 1
        case_count[side][label] += 1
        data_subsets[side].append(row)

    # Create children nodes.
    children = []
    for i in range(2):
        side = case_count[i]
        if is_pure_side(side):
            children.append({"label": side.index(max(side)) + 1})
        else:
            children.append(create_decision_tree(np.array(data_subsets[i])))

    return {"feature": feature_index + 1, "threshold": threshold, "children": children}


def test(dataset: Dataset, decision_tree: Node) -> float:
    """
    Test a decision tree on a dataset.

    Args:
        dataset: The dataset to test on.
        tree: The decision tree to test.

    Returns:
        The accuracy of decision.
    """
    correct_count = 0
    for row in dataset:
        node = decision_tree
        while "feature" in node:
            feature_index = node["feature"] - 1
            threshold = node["threshold"]
            side = 0 if row[feature_index] <= threshold else 1
            node = node["children"][side]

        decided_label = node["label"]
        actual_label = int(row[-1])
        if decided_label == actual_label:
            correct_count += 1
        else:
            print("Wrong decision:")
            print(f"Data: {list(row)}")
            print(f"Actual: {actual_label}")
            print(f"Decided: {decided_label}")
            print("---")

    row_count = dataset.shape[0]
    return correct_count / row_count * 100


if __name__ == "__main__":
    print(test_data)
    decision_tree = create_decision_tree(train_data)
    print(dumps(decision_tree, indent=2))

    accuracy = test(test_data, decision_tree)
    print(f"Accuracy: {accuracy}%")
