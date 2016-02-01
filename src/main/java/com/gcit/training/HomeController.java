package com.gcit.training;

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import com.gcit.training.lms.dao.BookDAO;
import com.gcit.training.lms.entity.Author;
import com.gcit.training.lms.entity.Book;
import com.gcit.training.lms.entity.Borrower;
import com.gcit.training.lms.entity.Genre;
import com.gcit.training.lms.entity.LibraryBranch;
import com.gcit.training.lms.entity.Publisher;
import com.gcit.training.lms.service.AdministrativeService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory
			.getLogger(HomeController.class);

	@Autowired
	AdministrativeService adminService;
	@Autowired
	BookDAO bookDao;

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG,
				DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate);

		return "home";
	}

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public String admin() {
		return "admin";
	}

	@RequestMapping(value = "/librarian", method = RequestMethod.GET)
	public String librarian(Locale locale, Model model) throws SQLException {
		return "librarian";
	}

	@RequestMapping(value = "/borrower", method = RequestMethod.GET)
	public String borrower(Locale locale, Model model) throws SQLException {
		return "borrower";
	}

	/*
	 * Authors Mapping
	 */

	@RequestMapping(value = "/viewauthor", method = RequestMethod.GET)
	public String viewAuthor(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {

		int count = adminService.getAllAuthorsCount();
		String s = adminService.pagination("/training/getAuthor", new String(),
				count, 10);

		model.addAttribute("pagination", s);

		return "viewauthor";

	}

	@RequestMapping(value = "/getAuthor", method = RequestMethod.GET)
	public String getAuthorData(Locale locale, Model model,
			WebRequest webRequest) throws SQLException {

		List<Author> authors;

		int pageNo = Integer.parseInt(webRequest.getParameter("pageNo"));
		int pageSize = Integer.parseInt(webRequest.getParameter("pageSize"));
		String searchString = webRequest.getParameter("searchString");

		authors = (List<Author>) adminService.getAllAuthors(pageNo, pageSize,
				searchString);

		StringBuffer sb = new StringBuffer(
				"<table class='table' id='authorsTable'><thead><tr><th>#</th><th>Author Name</th><th>Edit</th><th>Delete</th></tr></thead><tbody>");
		for (Author author : authors)
			sb.append("<tr><td>"
					+ author.getAuthorId()
					+ "</td><td>"
					+ author.getAuthorName()
					+ "</td><td align='center'><button type='button' class='btn btn btn-primary' data-toggle='modal' data-target='#myModal1' href='editAuthor?authorId="
					+ author.getAuthorId()
					+ "'>Edit Author</button></td><td><button type='button' class='btn btn-sm btn-danger' onclick=\"javascript:location.href='deleteAuthor?authorId="
					+ author.getAuthorId()
					+ "';\">Delete Author</button></td></tr>");

		sb.append("<table>");
		model.addAttribute("result", sb.toString());
		return "result";
	}

	@RequestMapping(value = "/searchAuthor", method = RequestMethod.GET)
	public String searchAuthor(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {

		String searchString = webRequest.getParameter("searchString");
		int count = adminService.searchAuthorsCount(searchString);
		String s = adminService.pagination("/training/getAuthor", searchString,
				count, 10);

		model.addAttribute("pagination", s);
		model.addAttribute("searchResult", searchString);
		return "viewauthor";

	}

	@RequestMapping(value = "/editAuthor", method = RequestMethod.GET)
	public String editAuthor(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {

		String authorId = webRequest.getParameter("authorId");
		if (authorId == null) {
			String error = "Please Provide Author Id";
			model.addAttribute("error", error);
			return error;
		}

		try {
			Author author = adminService.getAuthorById(Integer
					.parseInt(authorId));
			model.addAttribute("authorName", author.getAuthorName());
			model.addAttribute("authorId", authorId);
		} catch (Exception E) {
			String error = "Please Provide Author Id";
			model.addAttribute("error", error);
			return error;

		}
		return "editauthor";

	}

	@RequestMapping(value = "/editAuthor", method = RequestMethod.POST)
	public String editAuthorPost(Locale locale, Model model,
			WebRequest webRequest) throws SQLException {

		Integer authorId = Integer
				.parseInt(webRequest.getParameter("authorId"));
		String authorName = webRequest.getParameter("authorName");

		try {
			adminService.updateAuthor(authorId, authorName);
			model.addAttribute("status", "success");
		} catch (Exception E) {
			model.addAttribute("status", "Error");
		}
		return "status";

	}

	@RequestMapping(value = "/addAuthor", method = RequestMethod.GET)
	public String addAuthor(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {
		return "addAuthor";
	}

	@RequestMapping(value = "/addAuthor", method = RequestMethod.POST)
	public String addAuthorPost(Locale locale, Model model,
			WebRequest webRequest) throws SQLException {
		String authorName = webRequest.getParameter("authorName");

		try {
			adminService.addAuthor(authorName);
			model.addAttribute("status", "success");
		} catch (Exception E) {
			model.addAttribute("status", "Error");
		}
		return "status";
	}

	@RequestMapping(value = "/deleteAuthor", method = RequestMethod.GET)
	public String DeleteAuthor(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {
		int authorId = Integer.parseInt(webRequest.getParameter("authorId"));

		try {
			model.addAttribute("authorName",
					adminService.getAuthorById(authorId).getAuthorName());
			String status = adminService.deleteAuthor(authorId);
			model.addAttribute("status", status);

		} catch (Exception E) {
			model.addAttribute("status", "Error Delete Author");
		}
		return "deleteauthor";
	}

	/*
	 * Books Mapping
	 */
	@RequestMapping(value = "/viewBook", method = RequestMethod.GET)
	public String viewBook(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {

		int count = adminService.getAllBooksCount();
		String s = adminService.pagination("/training/getBook", new String(),
				count, 10);

		model.addAttribute("pagination", s);

		return "viewBook";

	}

	@RequestMapping(value = "/getBook", method = RequestMethod.GET)
	public String getBookData(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {

		List<Book> books;

		int pageNo = Integer.parseInt(webRequest.getParameter("pageNo"));
		int pageSize = Integer.parseInt(webRequest.getParameter("pageSize"));
		String searchString = webRequest.getParameter("searchString");

		books = (List<Book>) adminService.getAllBooks(pageNo, pageSize,
				searchString);

		StringBuffer sb = new StringBuffer(
				"<table class='table' id='booksTable'><thead><tr><th>#</th><th>Book Title</th><th>Book Publisher</th><th>Edit</th><th>Delete</th></tr></thead><tbody>");
		for (Book book : books)
			sb.append("<tr><td>"
					+ book.getBookId()
					+ "</td><td>"
					+ book.getTitle()
					+ "</td><td>"
					+ book.getPublisher().getPublisherName()
					+ "</td><td align='center'><button type='button' class='btn btn btn-primary' data-toggle='modal' data-target='#myModal1' href='editBook?bookId="
					+ book.getBookId()
					+ "'>Edit Book</button></td><td><button type='button' class='btn btn-sm btn-danger' onclick=\"javascript:location.href='deleteBook?bookId="
					+ book.getBookId() + "';\">Delete Book</button></td></tr>");

		sb.append("<table>");
		model.addAttribute("result", sb.toString());
		return "result";
	}

	@RequestMapping(value = "/searchBook", method = RequestMethod.GET)
	public String searchBook(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {

		String searchString = webRequest.getParameter("searchString");
		int count = adminService.searchBooksCount(searchString);
		String s = adminService.pagination("/training/getBook", searchString,
				count, 10);

		model.addAttribute("pagination", s);
		model.addAttribute("searchResult", searchString);
		return "viewBook";

	}

	@RequestMapping(value = "/editBook", method = RequestMethod.GET)
	public String editBook(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {

		String bookId = webRequest.getParameter("bookId");
		if (bookId == null) {
			String error = "Please Provide Book Id";
			model.addAttribute("error", error);
			return error;
		}

		try {
			Book book = adminService.getBookById(Integer.parseInt(bookId));
			model.addAttribute("BookTitle", book.getTitle());
			model.addAttribute("publisher", book.getPublisher()
					.getPublisherId());
			model.addAttribute("bookId", bookId);
		} catch (Exception E) {
			String error = "Please Provide Book Id";
			model.addAttribute("error", error);
			return error;

		}
		return "editBook";

	}

	@RequestMapping(value = "/editBook", method = RequestMethod.POST)
	public String editBookPost(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {

		Integer bookId = Integer.parseInt(webRequest.getParameter("bookId"));
		String bookTitle = webRequest.getParameter("BookTitle");

		try {
			adminService.updateBook(bookId, bookTitle);
			model.addAttribute("status", "success");
		} catch (Exception E) {
			model.addAttribute("status", "Error");
		}
		return "status";

	}

	@RequestMapping(value = "/addBook", method = RequestMethod.GET)
	public String addBook(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {
		return "addBook";
	}

	@RequestMapping(value = "/addBook", method = RequestMethod.POST)
	public String addBookPost(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {
		String bookTitle = webRequest.getParameter("bookTitle");
		int publisherId = Integer
				.parseInt(webRequest.getParameter("publisher"));
		Publisher p = new Publisher();
		p.setPublisherId(publisherId);

		try {
			adminService.addBook(bookTitle, p);
			model.addAttribute("status", "success");
		} catch (Exception E) {
			model.addAttribute("status", "Error");
		}
		return "status";
	}

	@RequestMapping(value = "/deleteBook", method = RequestMethod.GET)
	public String DeleteBook(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {
		int bookId = Integer.parseInt(webRequest.getParameter("bookId"));

		try {
			model.addAttribute("bookTitle", adminService.getBookById(bookId)
					.getTitle());
			String status = adminService.deleteBook(bookId);
			model.addAttribute("status", status);

		} catch (Exception E) {
			model.addAttribute("status", "Error Deleting Book");
		}
		return "deleteBook";
	}

	/*
	 * Branch Mapping
	 */
	@RequestMapping(value = "/viewBranch", method = RequestMethod.GET)
	public String viewBranch(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {

		int count = adminService.getAllBranchesCount();
		String s = adminService.pagination("/training/getBranch", new String(),
				count, 10);

		model.addAttribute("pagination", s);

		return "viewBranch";

	}

	@RequestMapping(value = "/getBranch", method = RequestMethod.GET)
	public String getBranchData(Locale locale, Model model,
			WebRequest webRequest) throws SQLException {

		List<LibraryBranch> lb;

		int pageNo = Integer.parseInt(webRequest.getParameter("pageNo"));
		int pageSize = Integer.parseInt(webRequest.getParameter("pageSize"));
		String searchString = webRequest.getParameter("searchString");

		lb = (List<LibraryBranch>) adminService.getAllBranches(pageNo,
				pageSize, searchString);

		StringBuffer sb = new StringBuffer(
				"<table class='table' id='booksTable'><thead><tr><th>#</th><th>Branch Name</th><th>Branch Address</th><th>Edit</th><th>Delete</th></tr></thead><tbody>");
		for (LibraryBranch lbs : lb)
			sb.append("<tr><td>"
					+ lbs.getBranchId()
					+ "</td><td>"
					+ lbs.getBranchName()
					+ "</td><td>"
					+ lbs.getBranchAddress()
					+ "</td><td align='center'><button type='button' class='btn btn btn-primary' data-toggle='modal' data-target='#myModal1' href='editBranch?branchId="
					+ lbs.getBranchId()
					+ "'>Edit Branch</button></td><td><button type='button' class='btn btn-sm btn-danger' onclick=\"javascript:location.href='deleteBranch?branchId="
					+ lbs.getBranchId()
					+ "';\">Delete Branch</button></td></tr>");

		sb.append("<table>");
		model.addAttribute("result", sb.toString());
		return "result";
	}

	@RequestMapping(value = "/searchBranch", method = RequestMethod.GET)
	public String searchBranch(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {

		String searchString = webRequest.getParameter("searchString");
		int count = adminService.searchBooksCount(searchString);
		String s = adminService.pagination("/training/getBranch", searchString,
				count, 10);

		model.addAttribute("pagination", s);
		model.addAttribute("searchResult", searchString);
		return "viewBranch";

	}

	@RequestMapping(value = "/editBranch", method = RequestMethod.GET)
	public String editBranch(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {

		String branchId = webRequest.getParameter("branchId");
		if (branchId == null) {
			String error = "Please Provide Branch Id1";
			model.addAttribute("error", error);
			return error;
		}

		try {
			LibraryBranch lb = adminService.getBranchById(Integer
					.parseInt(branchId));
			model.addAttribute("BranchName", lb.getBranchName());
			model.addAttribute("BranchAddress", lb.getBranchAddress());
			model.addAttribute("branchId", branchId);
		} catch (Exception E) {
			String error = "Please Provide Branch Id2";
			model.addAttribute("error", error);
			return error;

		}
		return "editBranch";

	}

	@RequestMapping(value = "/editBranch", method = RequestMethod.POST)
	public String editBranchPost(Locale locale, Model model,
			WebRequest webRequest) throws SQLException {

		Integer branchId = Integer
				.parseInt(webRequest.getParameter("branchId"));
		String BranchName = webRequest.getParameter("BranchName");
		String BranchAddress = webRequest.getParameter("BranchAddress");

		try {
			adminService.updateBranch(branchId, BranchName, BranchAddress);
			model.addAttribute("status", "success");
		} catch (Exception E) {
			model.addAttribute("status", "Error");
		}
		return "status";

	}

	@RequestMapping(value = "/addBranch", method = RequestMethod.GET)
	public String addBranch(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {
		return "addBranch";
	}

	@RequestMapping(value = "/addBranch", method = RequestMethod.POST)
	public String addBranchPost(Locale locale, Model model,
			WebRequest webRequest) throws SQLException {
		String BranchName = webRequest.getParameter("BranchName");
		String BranchAddress = webRequest.getParameter("BranchAddress");

		try {
			adminService.addBranch(BranchName, BranchAddress);
			model.addAttribute("status", "success");
		} catch (Exception E) {
			model.addAttribute("status", "Error");
		}
		return "status";
	}

	@RequestMapping(value = "/deleteBranch", method = RequestMethod.GET)
	public String deleteBranch(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {
		int branchId = Integer.parseInt(webRequest.getParameter("branchId"));

		try {
			model.addAttribute("brancName", adminService
					.getBranchById(branchId).getBranchName());
			String status = adminService.deleteBranch(branchId);
			model.addAttribute("status", status);

		} catch (Exception E) {
			model.addAttribute("status", "Error Deleting Book");
		}
		return "deleteBranch";
	}

	/*
	 * Borrower Mapping
	 */
	@RequestMapping(value = "/viewBorrower", method = RequestMethod.GET)
	public String viewBorrower(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {

		int count = adminService.getAllBorroersCount();
		String s = adminService.pagination("/training/getBorrower",
				new String(), count, 10);

		model.addAttribute("pagination", s);

		return "viewBorrower";

	}

	@RequestMapping(value = "/getBorrower", method = RequestMethod.GET)
	public String getBorrowerData(Locale locale, Model model,
			WebRequest webRequest) throws SQLException {

		List<Borrower> bor;

		int pageNo = Integer.parseInt(webRequest.getParameter("pageNo"));
		int pageSize = Integer.parseInt(webRequest.getParameter("pageSize"));
		String searchString = webRequest.getParameter("searchString");

		bor = (List<Borrower>) adminService.getAllBorrowers(pageNo, pageSize,
				searchString);

		StringBuffer sb = new StringBuffer(
				"<table class='table' id='borrowersTable'><thead><tr><th>#</th><th>Borrower Name</th><th>Borrower Address</th><th>Borrower phone</th><th>Edit</th><th>Delete</th></tr></thead><tbody>");
		for (Borrower bors : bor)
			sb.append("<tr><td>"
					+ bors.getCardNo()
					+ "</td><td>"
					+ bors.getName()
					+ "</td><td>"
					+ bors.getAddress()
					+ "</td><td>"
					+ bors.getPhone()
					+ "</td><td align='center'><button type='button' class='btn btn btn-primary' data-toggle='modal' data-target='#myModal1' href='editBorrower?borId="
					+ bors.getCardNo()
					+ "'>Edit Borrower</button></td><td><button type='button' class='btn btn-sm btn-danger' onclick=\"javascript:location.href='deleteBorrower?borId="
					+ bors.getCardNo()
					+ "';\">Delete Borrower</button></td></tr>");

		sb.append("<table>");
		model.addAttribute("result", sb.toString());
		return "result";
	}

	@RequestMapping(value = "/searchBorrower", method = RequestMethod.GET)
	public String searchBorrower(Locale locale, Model model,
			WebRequest webRequest) throws SQLException {

		String searchString = webRequest.getParameter("searchString");
		int count = adminService.searchBooksCount(searchString);
		String s = adminService.pagination("/training/getBorrower",
				searchString, count, 10);

		model.addAttribute("pagination", s);
		model.addAttribute("searchResult", searchString);
		return "viewBorrower";

	}

	@RequestMapping(value = "/editBorrower", method = RequestMethod.GET)
	public String editBorrower(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {

		String borId = webRequest.getParameter("borId");
		if (borId == null) {
			String error = "Please Provide Borrower ID";
			model.addAttribute("error", error);
			return error;
		}

		try {
			Borrower bor = adminService
					.getBorrowerById(Integer.parseInt(borId));
			model.addAttribute("borName", bor.getName());
			model.addAttribute("borAddress", bor.getAddress());
			model.addAttribute("borPhone", bor.getPhone());
			model.addAttribute("borId", borId);
		} catch (Exception E) {
			String error = "Please Provide Borrower Id2";
			model.addAttribute("error", error);
			return error;

		}
		return "editBorrower";

	}

	@RequestMapping(value = "/editBorrower", method = RequestMethod.POST)
	public String editBorrowerPost(Locale locale, Model model,
			WebRequest webRequest) throws SQLException {

		Integer borId = Integer.parseInt(webRequest.getParameter("borId"));
		String borName = webRequest.getParameter("borName");
		String borAddress = webRequest.getParameter("borAddress");
		String borPhone = webRequest.getParameter("borPhone");

		try {
			adminService.updateBorrower(borId, borName, borAddress, borPhone);
			model.addAttribute("status", "success");
		} catch (Exception E) {
			model.addAttribute("status", "Error");
		}
		return "status";

	}

	@RequestMapping(value = "/addBorrower", method = RequestMethod.GET)
	public String addBorrower(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {
		return "addBorrower";
	}

	@RequestMapping(value = "/addBorrower", method = RequestMethod.POST)
	public String addBorrowerPost(Locale locale, Model model,
			WebRequest webRequest) throws SQLException {
		String borName = webRequest.getParameter("borName");
		String borAddress = webRequest.getParameter("borAddress");
		String borPhone = webRequest.getParameter("borPhone");

		try {
			adminService.addBorrower(borName, borAddress, borPhone);
			model.addAttribute("status", "success");
		} catch (Exception E) {
			model.addAttribute("status", "Error");
		}
		return "status";
	}

	@RequestMapping(value = "/deleteBorrower", method = RequestMethod.GET)
	public String DeleteBorrower(Locale locale, Model model,
			WebRequest webRequest) throws SQLException {
		int borId = Integer.parseInt(webRequest.getParameter("borId"));

		try {
			model.addAttribute("borName", adminService.getBorrowerById(borId)
					.getName());
			String status = adminService.deleteborrower(borId);
			model.addAttribute("status", status);

		} catch (Exception E) {
			model.addAttribute("status", "Error Deleting Book");
		}
		return "deleteBorrower";
	}

	/*
	 * BookBorrowing Mapping
	 */
	@RequestMapping(value = "/viewBookb", method = RequestMethod.GET)
	public String viewBookb(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {

		int count = adminService.getAllBooksCount();
		String s = adminService.pagination("/training/getBookb", new String(),
				count, 10);

		model.addAttribute("pagination", s);

		return "viewBookb";

	}

	@RequestMapping(value = "/getBookb", method = RequestMethod.GET)
	public String getBookbData(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {

		List<Book> books;

		int pageNo = Integer.parseInt(webRequest.getParameter("pageNo"));
		int pageSize = Integer.parseInt(webRequest.getParameter("pageSize"));
		String searchString = webRequest.getParameter("searchString");

		books = (List<Book>) adminService.getAllBooks(pageNo, pageSize,
				searchString);

		StringBuffer sb = new StringBuffer(
				"<table class='table' id='booksTable'><thead><tr><th>#</th><th>Book Title</th><th>Book Publisher</th><th>Action</th></tr></thead><tbody>");
		for (Book book : books)
			sb.append("<tr><td>"
					+ book.getBookId()
					+ "</td><td>"
					+ book.getTitle()
					+ "</td><td>"
					+ book.getPublisher().getPublisherName()
					+ "</td><td align='center'><button type='button' class='btn btn btn-primary' data-toggle='modal' data-target='#myModal1' href='borrowBookb?bookId="
					+ book.getBookId() + "'>Borrow Book</button></td></tr>");

		sb.append("<table>");
		model.addAttribute("result", sb.toString());
		return "result";
	}

	@RequestMapping(value = "/searchBookb", method = RequestMethod.GET)
	public String searchBookb(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {

		String searchString = webRequest.getParameter("searchString");
		int count = adminService.searchBooksCount(searchString);
		String s = adminService.pagination("/training/getBook", searchString,
				count, 10);

		model.addAttribute("pagination", s);
		model.addAttribute("searchResult", searchString);
		return "viewBookb";

	}

	@RequestMapping(value = "/borrowBookb", method = RequestMethod.GET)
	public String borrowBookb(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {

		String bookId = webRequest.getParameter("bookId");
		if (bookId == null) {
			String error = "Please Provide book ID";
			model.addAttribute("error", error);
			return error;
		}

		try {

		} catch (Exception E) {
			String error = "Please Provide Borrower Id2";
			model.addAttribute("error", error);
			return error;

		}
		return "borrowBookb";

	}

	@RequestMapping(value = "/borrowBookb", method = RequestMethod.POST)
	public String borrowBookbPost(Locale locale, Model model,
			WebRequest webRequest) throws SQLException {

		try {
			model.addAttribute("status", "success");
		} catch (Exception E) {
			model.addAttribute("status", "Error");
		}
		return "status";

	}

	/*
	 * Genre Mapping
	 */

	@RequestMapping(value = "/viewGenre", method = RequestMethod.GET)
	public String viewGenre(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {

		int count = adminService.getAllGenreCount();
		String s = adminService.pagination("/training/getGenre", new String(),
				count, 10);

		model.addAttribute("pagination", s);

		return "viewgenre";

	}

	@RequestMapping(value = "/getGenre", method = RequestMethod.GET)
	public String getGenreData(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {

		List<Genre> genres;

		int pageNo = Integer.parseInt(webRequest.getParameter("pageNo"));
		int pageSize = Integer.parseInt(webRequest.getParameter("pageSize"));
		String searchString = webRequest.getParameter("searchString");

		genres = (List<Genre>) adminService.getAllGenre(pageNo, pageSize,
				searchString);

		StringBuffer sb = new StringBuffer(
				"<table class='table' id='authorsTable'><thead><tr><th>#</th><th>Author Name</th><th>Edit</th><th>Delete</th></tr></thead><tbody>");
		for (Genre genre : genres)
			sb.append("<tr><td>"
					+ genre.getGenreId()
					+ "</td><td>"
					+ genre.getGenreName()
					+ "</td><td align='center'><button type='button' class='btn btn btn-primary' data-toggle='modal' data-target='#myModal1' href='editgenre?genreId="
					+ genre.getGenreId()
					+ "'>Edit Genre</button></td><td><button type='button' class='btn btn-sm btn-danger' onclick=\"javascript:location.href='deletegenre?genreId="
					+ genre.getGenreId()
					+ "';\">Delete Genre</button></td></tr>");

		sb.append("<table>");
		model.addAttribute("result", sb.toString());
		return "result";
	}

	@RequestMapping(value = "/searchGenre", method = RequestMethod.GET)
	public String searchGenre(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {

		String searchString = webRequest.getParameter("searchString");
		int count = adminService.searchGenreCount(searchString);
		String s = adminService.pagination("/training/getGenre", searchString,
				count, 10);

		model.addAttribute("pagination", s);
		model.addAttribute("searchResult", searchString);
		return "viewGenre";

	}

	@RequestMapping(value = "/editgenre", method = RequestMethod.GET)
	public String editGenre(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {

		String genreId = webRequest.getParameter("genreId");
		if (genreId == null) {
			String error = "Please Provide Genre Id";
			model.addAttribute("error", error);
			return error;
		}

		try {
			Genre genre = adminService.getGenreById(Integer.parseInt(genreId));
			model.addAttribute("genreName", genre.getGenreName());
			model.addAttribute("genreId", genreId);
		} catch (Exception E) {
			String error = "Please Provide Genre Id";
			model.addAttribute("error", error);
			return error;

		}
		return "editgenre";

	}

	@RequestMapping(value = "/editgenre", method = RequestMethod.POST)
	public String editGenrePost(Locale locale, Model model,
			WebRequest webRequest) throws SQLException {

		Integer genreId = Integer.parseInt(webRequest.getParameter("genreId"));
		String genreName = webRequest.getParameter("genreName");

		try {
			adminService.updateGenre(genreId, genreName);
			model.addAttribute("status", "success");
		} catch (Exception E) {
			model.addAttribute("status", "Error");
		}
		return "status";

	}

	@RequestMapping(value = "/addGenre", method = RequestMethod.GET)
	public String addGenre(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {
		return "addGenre";
	}

	@RequestMapping(value = "/addGenre", method = RequestMethod.POST)
	public String addGenrePost(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {
		
		String genreName = webRequest.getParameter("genreName");

		try {
			adminService.addgenre(genreName);
			model.addAttribute("status", "success");
		} catch (Exception E) {
			model.addAttribute("status", "Error");
		}
		return "status";
	}

	@RequestMapping(value = "/deleteGenre", method = RequestMethod.GET)
	public String DeleteGenre(Locale locale, Model model, WebRequest webRequest)
			throws SQLException {
		int genreId = Integer.parseInt(webRequest.getParameter("genreId"));
		try {
			model.addAttribute("genreName", adminService.getGenreById(genreId)
					.getGenreName());
			String status = adminService.deleteGenre(genreId);
			model.addAttribute("status", status);

		} catch (Exception E) {
			model.addAttribute("status", "Error Delete Genre");
		}
		return "deleteGenre";
	}

	/*
	 * testing stuff
	 */
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String test(Locale locale, Model model) throws SQLException {

		Book book = bookDao.readOne(2);
		model.addAttribute("book", book.getTitle());
		return "test";
	}

}