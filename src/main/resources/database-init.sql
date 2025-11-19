DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS menu_items;
DROP TABLE IF EXISTS restaurants;
DROP TABLE IF EXISTS carts;
DROP TABLE IF EXISTS authorities;
DROP TABLE IF EXISTS customers;

CREATE TABLE customers
(
    id SERIAL PRIMARY KEY NOT NULL,
    email TEXT UNIQUE NOT NULL,
    enabled BOOLEAN DEFAULT TRUE NOT NULL,
    password TEXT NOT NULL,
    first_name TEXT,
    last_name TEXT
);

CREATE TABLE authorities
(
    id SERIAL PRIMARY KEY NOT NULL,
    email TEXT NOT NULL,
    authority TEXT NOT NULL,
    CONSTRAINT fk_customer FOREIGN KEY (email) REFERENCES customers (email) ON DELETE CASCADE
);

CREATE TABLE carts
(
    id SERIAL PRIMARY KEY NOT NULL,
    customer_id INTEGER UNIQUE NOT NULL,
    total_price NUMERIC NOT NULL,
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE CASCADE
);

CREATE TABLE restaurants
(
    id SERIAL PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    address TEXT,
    image_url TEXT,
    phone TEXT
);

CREATE TABLE menu_items
(
    id SERIAL PRIMARY KEY NOT NULL,
    restaurant_id INTEGER NOT NULL,
    name TEXT NOT NULL,
    price NUMERIC NOT NULL,
    description TEXT,
    image_url TEXT,
    CONSTRAINT fk_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants (id) ON DELETE CASCADE
);

CREATE TABLE order_items
(
    id SERIAL PRIMARY KEY NOT NULL,
    menu_item_id INTEGER NOT NULL,
    cart_id INTEGER NOT NULL,
    price NUMERIC NOT NULL,
    quantity INTEGER NOT NULL,
    CONSTRAINT fk_cart FOREIGN KEY (cart_id) REFERENCES carts (id) ON DELETE CASCADE,
    CONSTRAINT fk_menu_item FOREIGN KEY (menu_item_id) REFERENCES menu_items (id) ON DELETE CASCADE
);

INSERT INTO restaurants (name, address, image_url, phone)
VALUES ('Burger King', '773 N Mathilda Ave, Sunnyvale, CA 94085',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/store%2Fheader%2F102117.png',
        '(408) 736-0101'),
       ('SGD Tofu House','3450 El Camino Real #105, Santa Clara, CA 95051',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/store%2F1579.jpg',
        '(408) 261-3030'),
       ('Fashion Wok', '163 S Murphy Ave, Sunnyvale, CA 94086',
        'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/store%2F273997.jpg',
        '(408) 739-8866');

INSERT INTO menu_items (description, image_url, name, price, restaurant_id)
VALUES
    ('Made with white meat chicken, our Chicken Fries are coated in a light crispy breading seasoned with savory spices and herbs.',
     'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=300,format=auto,quality=50/https://cdn.doordash.com/media/photos/4f43946f-c5ab-47af-bacf-7b3ba60a24b-retina-large.jpg',
     'Chicken Fries - 9 Pc', 4.89, 1),
    ('Our Whopper Sandwich is a 1/4 lb* of savory flame-grilled beef topped with juicy tomatoes, fresh lettuce, creamy mayonnaise, ketchup, crunchy pickles, and sliced white onions on a soft sesame seed bun.',
     'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=300,format=auto,quality=50/https://cdn.doordash.com/media/photos/878a6889-6150-4a0e-a0ff-e2fb1f324690-retina-large.jpg',
     'Whopper Meal', 10.59, 1),
    ('Our Impossible™ Whopper Sandwich features a savory flame-grilled patty made from plants topped with juicy tomatoes, fresh lettuce, creamy mayonnaise, ketchup, crunchy pickles, and sliced white onions on a soft sesame seed bun.',
     'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=300,format=auto,quality=50/https://cdn.doordash.com/media/photos/5c306a5f-fdd2-41d2-a660-9762aaa8eee8-retina-large.jpg',
     'Impossible™ Whopper', 7.99, 1),
    ('Say hello to our HERSHEY’S® Sundae Pie. One part crunchy chocolate crust and one part chocolate crème filling, garnished with a delicious topping and real HERSHEY’S® Chocolate Chips',
     'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/80b1670d-e9c0-4886-a5b7-1ad48ed24ca-retina-large.jpg',
     'HERSHEY''S® Sundae Pie', 3.09, 1),
    ('Our Whopper Sandwich is a pairing of two 1/4 lb* savory flame-grilled beef patties topped with juicy tomatoes, fresh lettuce, creamy mayonnaise, ketchup, crunchy pickles, and sliced white onions on a soft sesame seed bun.',
     'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/9b3d7985-e457-43b3-938d-5184f48c2687-retina-large.jpg',
     'Double Whopper Meal', 11.69, 1),
    ('Our Original Chicken Sandwich is lightly breaded and topped with a simple combination of shredded lettuce and creamy mayonnaise on a sesame seed bun.',
     'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/31dda882-06ec-42ad-bcd4-da7bd342574f-retina-large.jpeg',
     'Original Chicken Sandwich', 6.09, 1),
    ('Cool down with our creamy hand spun OREO® Shake.',
     'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=300,format=auto,quality=50/https://cdn.doordash.com/media/photos/5b43852e-d253-461c-8be8-1bb0b5e5e39be-retina-large.jpeg',
     'OREO® Shake', 3.99, 1),
    ('Tofu boiled with your choice of meat and mushrooms. Served with your choice of side and an assortment of kimchi dishes.',
     'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/b7055ca9-3caf-49d9-9c99-04be1e36dbbf-retina-large-jpeg',
     'Original Soft Tofu', 17.06, 2),
    ('Tofu boiled with beef, shrimp, and clams. Served with your choice of side and an assortment of kimchi dishes.',
     'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/37ad1974-1395-4e5c-86ff-fdf120c8f8c5-retina-large-jpeg',
     'Combination Soft Tofu', 17.06, 2),
    ('Tofu boiled with mussels, shrimp, and clam. Served with your choice of side and an assortment of kimchi dishes.',
     'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/96bc8289-1950-4b4f-823d-1e7333495afe-retina-large-jpeg',
     'Seafood Soft Tofu', 17.06, 2),
    ('三味茄子Eggplant with Minced Pork, Garlic, Cilantro',
     'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/b7f0f262-0c55-41e1-89bc-84c061e34ef5-retina-large.jpg',
     '三味茄子Eggplant with Minced Pork', 14.99, 3),
    ('大白菜Cauliflower Stir Fried with Meat',
     'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/c8b07c77-ace1-49ec-aa42-9e18de102224-retina-large.jpg',
     '大白菜 Cauliflower', 14.99, 3),
    ('酸菜鱼片Poached Fish Fillets in Sour Soup',
     'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/efc93f80-189d-458a-3157-fe215a283d9b-retina-large.jpg',
     '酸菜鱼片', 17.99, 3),
    ('小炒黄牛肉Stir Fried Beef with Pepper',
     'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/7f0553d8-5e83-47d6-a45a-73a3eb8a94e0-retina-large.jpg',
     '小炒黄牛肉', 16.99, 3),
    ('江湖香干炒牛丝Stir Fried Shredded Tripe with Wugang Tofu',
     'https://img.cdn4dd.com/cdn-cgi/image/fit=contain,width=1920,format=auto,quality=50/https://cdn.doordash.com/media/photos/8b2ca9fc-2c1d-4bf2-96ff-d0b3c415e8d-retina-large.jpg',
     '江湖香干炒牛丝', 19.99, 3);
