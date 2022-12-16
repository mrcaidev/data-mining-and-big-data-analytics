import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt

iris = pd.read_csv("data/preprocessing/iris.csv")


def scatter(group: str):
    sns.relplot(
        data=iris,
        x=f"{group}width",
        y=f"{group}length",
        hue="class",
        palette=("red", "green", "blue"),
    )
    plt.savefig(f"outputs/preprocessing/iris-{group}-scatter.png")
    plt.clf()


def hist(attribute: str):
    sns.histplot(
        data=iris,
        x=attribute,
        hue="class",
        multiple="stack",
    )
    plt.savefig(f"outputs/preprocessing/iris-{attribute}-hist.png")
    plt.clf()


if __name__ == "__main__":
    scatter("sepal")
    scatter("petal")
    hist("sepalwidth")
    hist("sepallength")
    hist("petallength")
    hist("petalwidth")
