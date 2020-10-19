package observer;

public interface Observable {
	void addObserser(Observer o);
	void removeObserser(Observer o);
	void notifyObserser();
}
