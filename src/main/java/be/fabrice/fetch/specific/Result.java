package be.fabrice.fetch.specific;

public class Result{
	private Reviewer reviewer;
	private Review review;
	
	public Result(Reviewer reviewer, Review review) {
		this.reviewer = reviewer;
		this.review = review;
	}

	public Reviewer getReviewer() {
		return reviewer;
	}

	public Review getReview() {
		return review;
	}
}