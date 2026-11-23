from typing import Union

def main() -> None:
  # Create menu
  menu = create_items()

  # Get customer's orders
  customer_orders = get_customer_orders()

  # Join customer items to a readable string
  customer_items = ', '.join(map(
    lambda order: ' of '.join([str(order['quantity']), order['order']]),
    customer_orders
  ))

  # Calculate the total cost of customer's orders
  total = 0
  for order in customer_orders:
    order, quantity = order['order'], order['quantity']
    price = list(filter(lambda item: item['name'] == order, menu))[0]['price']
    total = total + (price*quantity)

  print(f"Customer bought {customer_items}. Total cost: ${total:.2f}")

def get_customer_orders() -> list[dict]:
  total_orders = get_user_input('How many orders will you make?', int);

  customer_orders = []
  for _ in range(total_orders):
    order = get_user_input('What is your order?', str)
    quantity = get_user_input(f"How many {order}'s do you want?", int)
    customer_orders.append({ "order": order, "quantity": quantity })

  return customer_orders

def get_user_input(ask: str, data_type: type) -> Union[str, int, float]:
  while True:
    response = input(ask + ": ")
    try:
      return data_type(response)
    except ValueError:
      continue

def create_item(name: str, price: float, available: bool) -> dict:
  return { 'name': name, 'price': price, 'available': available }

def create_items() -> list[dict]:
  cappuccino = create_item('cappuccino', 45.0, True)
  mocha = create_item('mocha', 65.0, True)
  latte = create_item('chai latte', 50.5, True)
  choco = create_item('hot chocolate', 50.0, False)
  return [cappuccino, mocha, latte, choco]

if __name__ == '__main__': 
  main()