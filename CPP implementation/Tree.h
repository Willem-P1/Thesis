#include <iostream>
#include <unordered_map>
#include <vector>

using namespace std;

class Tree
{
    private:
    unordered_map<int, vector<int>> graph;


    public:
    map<int, vector<int>> getTree(){return graph;}

    vector<int> getNode(int label){return graph.find(label);}

    void addNode(int label){graph.insert(label);}

    void removeNode(int label){
        vector<int> edges = getNode(label);
        graph.erase(label);
        for(int i = 0; i < edges.size(); i++)
        {
            vector<int> vec = getNode(edges[i]);
            vec.erase(remove(vec.begin(), vec.end(), label), vec.end());
        }
    }

    void addEdge(int from, int to)
    {
        getNode(from).push_back(to);
        getNode(to).push_back(from);
    }

    void removeEdge(int from, int to)
    {
        vector<int> vec = getNode(from);
        vec.erase(remove(vec.begin(), vec.end(), to), vec.end());
        
        vec = getNode(to);
        vec.erase(remove(vec.begin(), vec.end(), from), vec.end());
    }

    void bisectEdge(int from, int to, int label)
    {
        removeEdge(from, to);
        addNode(label);
        addEdge(from, label);
        addEdge(label, to);
    }
};