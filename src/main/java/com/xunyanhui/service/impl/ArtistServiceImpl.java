package com.xunyanhui.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.xunyanhui.bean.ArtistListV;
import com.xunyanhui.bean.HomeArtist;
import com.xunyanhui.dao.ArtistDao;
import com.xunyanhui.dao.SystemDao;
import com.xunyanhui.dao.UserDao;
import com.xunyanhui.model.Artist;
import com.xunyanhui.model.ArtistDetailView;
import com.xunyanhui.model.User;
import com.xunyanhui.service.ArtistService;

@Service
public class ArtistServiceImpl implements ArtistService {
	private static final int HOME_PAGE_COUNT = 5;
	private static final int ARTIST_PAGE_COUNT = 5;

	final Logger logger = Logger.getLogger("ArtistService");
	@Autowired
	private UserDao userDao;
	@Autowired
	private SystemDao systemDao;
	@Autowired
	private ArtistDao artistDao;


	@Override
	public List<HomeArtist> getreCommendArtistList(Date date, int page) {
		// TODO Auto-generated method stub
		List<User> homeArtistList = userDao.getHomeArtistList(date, (page - 1)
				* HOME_PAGE_COUNT, HOME_PAGE_COUNT);
		HomeArtist ha = null;
		List<HomeArtist> haList = new ArrayList<HomeArtist>();
		for (User user : homeArtistList) {
			ha = new HomeArtist();
			Artist ar = user.getArtist();
			ha.setId(user.getId());
			ha.setAddress(ar.getAddress());
			ha.setCompleteNum(ar.getCompleteNum());
			ha.setFanNum(ar.getFanNum());
			ha.setSpecialty(ar.getSpecialty());
			ha.setStageName(ar.getStageName());
			ha.setMinSalary(ar.getMinSalary().toString());
			haList.add(ha);

		}

		return haList;
	}

	@Override
	public List<ArtistListV> getArtiArtistList(Date date, int order,
			String type, int gender, int page) {
		// TODO Auto-generated method stub
		List<User> userList = userDao.getArtiArtistList(date, order, type,
				gender, (page - 1) * ARTIST_PAGE_COUNT, ARTIST_PAGE_COUNT);
		ArtistListV artist = null;
		List<ArtistListV> artistList = new ArrayList<ArtistListV>();

		for (User user : userList) {
			artist = new ArtistListV();
			Artist a = user.getArtist();
			artist.setId(a.getId());
			artist.setCompleteNum(a.getCompleteNum());
			artist.setFanNum(a.getFanNum());
			artist.setMinSalary(a.getMinSalary());
			artist.setStageName(a.getStageName());
			artist.setV(user.isV());
			artist.setCity(a.getCity());

			artist.setIsAuth(a.getIsAuth());
			artist.setHonestyLevel(a.getHonestyLevel());
			artist.setPerformanceType(a.getPerformanceTypeId());
			artistList.add(artist);

		}

		return artistList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xunyanhui.service.ArtistService#getArtistById(java.lang.String)
	 */
	@Override
	public ArtistDetailView getArtistById(String id, String uid) {
		ArtistDetailView list = artistDao.getAtristById(id, uid);
		return list;
	}

	@Override
	public String follow(String perfId, User user) {
		// TODO Auto-generated method stub
		String result = null;
		if (user == null) {
			result = "{\"code\":-1}";
		} else if (true) {

		}
		return null;
	}

	@Override
	public List<ArtistListV> search(String key, Date date, int page) {
		// TODO Auto-generated method stub
		/*
		 * if (key == null || key.equals("")) { return null; }
		 */
		List<User> userList = userDao.searchByKey("%" + key + "%", date,
				(page - 1) * ARTIST_PAGE_COUNT, ARTIST_PAGE_COUNT);
		ArtistListV artist = null;
		List<ArtistListV> artistList = new ArrayList<ArtistListV>();
		if (userList != null) {
			for (User user : userList) {
				artist = new ArtistListV();
				Artist a = user.getArtist();
				artist.setId(a.getId());
				artist.setCompleteNum(a.getCompleteNum());
				artist.setFanNum(a.getFanNum());
				artist.setMinSalary(a.getMinSalary());
				artist.setStageName(a.getStageName());
				artist.setV(user.isV());
				artist.setPerformanceType(a.getPerformanceTypeId());
				artist.setIsAuth(a.getIsAuth());
				artist.setHonestyLevel(a.getHonestyLevel());
				artist.setCity(a.getCity());
				artist.setPerformanceType(a.getPerformanceTypeId());
				artistList.add(artist);
			}
		}
		return artistList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xunyanhui.service.ArtistService#getAtristByIdUnLogin(java.lang.String
	 * , java.lang.String)
	 */
	@Override
	public ArtistDetailView getAtristByIdUnLogin(String id, String uid) {
		ArtistDetailView list = artistDao.getAtristByIdUnLogin(id, uid);

		return list;
	}

}
