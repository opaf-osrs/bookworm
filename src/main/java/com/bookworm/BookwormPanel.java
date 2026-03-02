package com.bookworm;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class BookwormPanel extends PluginPanel
{
	private final JLabel relayLabel   = new JLabel("● Relay: connecting...");
	private final JLabel statusLabel  = new JLabel("Not logged in");
	private final JLabel countLabel   = new JLabel("0 books collected");
	private final DefaultListModel<String> bookListModel = new DefaultListModel<>();
	private final JList<String> bookList = new JList<>(bookListModel);
	private int totalBooks = 0;

	private Runnable onReset;
	private Runnable onTestPopup;

	/** Set the callback that fires when the Reset button is clicked. */
	public void setOnReset(Runnable callback)
	{
		onReset = callback;
	}

	/** Set the callback that fires when the Test Popup button is clicked. */
	public void setOnTestPopup(Runnable callback)
	{
		onTestPopup = callback;
	}

	public BookwormPanel()
	{
		setLayout(new BorderLayout(0, 8));
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setBorder(new EmptyBorder(10, 10, 10, 10));

		// ── Header ────────────────────────────────────────────────────────────
		JPanel header = new JPanel(new BorderLayout(0, 4));
		header.setBackground(ColorScheme.DARK_GRAY_COLOR);

		JLabel title = new JLabel("Bookworm");
		title.setFont(FontManager.getRunescapeBoldFont());
		title.setForeground(Color.WHITE);

		relayLabel.setFont(FontManager.getRunescapeSmallFont());
		relayLabel.setForeground(Color.YELLOW);

		statusLabel.setFont(FontManager.getRunescapeSmallFont());
		statusLabel.setForeground(Color.GRAY);

		countLabel.setFont(FontManager.getRunescapeSmallFont());
		countLabel.setForeground(new Color(0x90ee90));

		JPanel headerLabels = new JPanel(new GridLayout(3, 1, 0, 2));
		headerLabels.setBackground(ColorScheme.DARK_GRAY_COLOR);
		headerLabels.add(relayLabel);
		headerLabels.add(statusLabel);
		headerLabels.add(countLabel);

		header.add(title,        BorderLayout.NORTH);
		header.add(headerLabels, BorderLayout.CENTER);

		// ── Book list ─────────────────────────────────────────────────────────
		bookList.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		bookList.setForeground(Color.LIGHT_GRAY);
		bookList.setFont(FontManager.getRunescapeSmallFont());
		bookList.setSelectionBackground(ColorScheme.BRAND_ORANGE_TRANSPARENT);

		JScrollPane scroll = new JScrollPane(bookList);
		scroll.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		scroll.setBorder(BorderFactory.createLineBorder(ColorScheme.MEDIUM_GRAY_COLOR));

		// ── Buttons ───────────────────────────────────────────────────────────
		JButton testBtn = new JButton("Test popup");
		testBtn.setFont(FontManager.getRunescapeSmallFont());
		testBtn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		testBtn.setForeground(new Color(0x90ee90));
		testBtn.setFocusPainted(false);
		testBtn.addActionListener(e -> { if (onTestPopup != null) onTestPopup.run(); });

		JButton resetBtn = new JButton("Reset seen books");
		resetBtn.setFont(FontManager.getRunescapeSmallFont());
		resetBtn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		resetBtn.setForeground(Color.LIGHT_GRAY);
		resetBtn.setFocusPainted(false);
		resetBtn.addActionListener(e -> { if (onReset != null) onReset.run(); });

		JPanel btnPanel = new JPanel(new GridLayout(2, 1, 0, 4));
		btnPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		btnPanel.add(testBtn);
		btnPanel.add(resetBtn);

		add(header,   BorderLayout.NORTH);
		add(scroll,   BorderLayout.CENTER);
		add(btnPanel, BorderLayout.SOUTH);
	}

	/** Updates the relay connection indicator. */
	public void setRelayStatus(boolean connected)
	{
		SwingUtilities.invokeLater(() ->
		{
			relayLabel.setText(connected ? "● Relay: connected" : "● Relay: disconnected");
			relayLabel.setForeground(connected ? new Color(0x90ee90) : Color.RED);
		});
	}

	/** Called when the player logs in or the plugin connects. */
	public void setPlayerName(String name)
	{
		SwingUtilities.invokeLater(() ->
			statusLabel.setText(name != null ? "Player: " + name : "Not logged in"));
	}

	/** Replaces the displayed book list with the current collection. */
	public void updateBooks(List<String> bookNames, int total)
	{
		SwingUtilities.invokeLater(() ->
		{
			totalBooks = total;
			bookListModel.clear();
			for (String name : bookNames) bookListModel.addElement(name);
			countLabel.setText(bookNames.size() + " / " + totalBooks + " books collected");
		});
	}

	/** Adds a single book to the displayed list in sorted order and updates the count. */
	public void addBook(String bookName)
	{
		SwingUtilities.invokeLater(() ->
		{
			if (bookListModel.contains(bookName)) return;

			// Insert in alphabetical order
			int insertAt = bookListModel.getSize();
			for (int i = 0; i < bookListModel.getSize(); i++)
			{
				if (bookName.compareToIgnoreCase(bookListModel.getElementAt(i)) < 0)
				{
					insertAt = i;
					break;
				}
			}
			bookListModel.insertElementAt(bookName, insertAt);
			countLabel.setText(bookListModel.getSize() + " / " + totalBooks + " books collected");
		});
	}
}
